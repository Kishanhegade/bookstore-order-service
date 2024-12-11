package com.bridgelabz.bsa.orderservice.service;


import com.bridgelabz.bsa.orderservice.exception.InvalidQuantityException;
import com.bridgelabz.bsa.orderservice.exception.OrderNotFoundByIdException;
import com.bridgelabz.bsa.orderservice.feign.BookServiceFeignClient;
import com.bridgelabz.bsa.orderservice.feign.CartServiceFeignClient;
import com.bridgelabz.bsa.orderservice.mapper.OrderMapper;
import com.bridgelabz.bsa.orderservice.model.Order;
import com.bridgelabz.bsa.orderservice.model.OrderBook;
import com.bridgelabz.bsa.orderservice.repository.OrderRepository;
import com.bridgelabz.bsa.orderservice.requestdto.OrderRequest;
import com.bridgelabz.bsa.orderservice.responsedto.BookResponse;
import com.bridgelabz.bsa.orderservice.responsedto.CartResponse;
import com.bridgelabz.bsa.orderservice.responsedto.OrderResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepo;
    private CartServiceFeignClient cartService;
    private BookServiceFeignClient bookService;
    private OrderMapper orderMapper;


    @Transactional
    public OrderResponse placeOrderByUser(Long userId, OrderRequest orderRequest) {


        List<CartResponse> cartResponses = cartService.getAllCartItemsForUser(userId.toString()).getBody();

        double totalPrice = cartResponses.stream().mapToDouble(cartResponse -> {
            long bookId = cartResponse.getBookId();
            BookResponse bookResponse = bookService.getBookById(bookId).getBody();
            if (cartResponse.getQuantity() > bookResponse.getQuantity()) {
                throw new InvalidQuantityException("Not enough stock for book: " + bookResponse.getBookName()+ " in cart " + cartResponse.getCartId());
            }
            return cartResponse.getTotalPrice();
        }).sum();

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDate.now());
        order.setPrice(totalPrice);
        order.setAddress(orderRequest.getAddress());
        order.setCancel(false);

        List<OrderBook> orderBooks = cartResponses.stream().map(cartResponse -> {
            Long bookId = cartResponse.getBookId();
            BookResponse bookResponse = bookService.getBookById(bookId).getBody();
            bookResponse.setQuantity(bookResponse.getQuantity() - cartResponse.getQuantity());
            bookService.changeBookQuantity(bookId,bookResponse.getQuantity());

            order.setQuantity(cartResponse.getQuantity());

            OrderBook orderBook = new OrderBook();
            orderBook.setOrder(order);
            orderBook.setBookId(cartResponse.getBookId());
            orderBook.setQuantity(cartResponse.getQuantity());
            return orderBook;
        }).toList();

        order.setOrderBooks(orderBooks);

        Order savedOrder = orderRepo.save(order);

        cartService.removeFromCartByUserId(userId.toString());

        return orderMapper.mapToOrderResponse(savedOrder);
    }


    @Transactional
    public OrderResponse placeOrderByCartId(Long userId, long cartId, OrderRequest orderRequest) {

        CartResponse cart = cartService.getCartByCartId(cartId).getBody();

        Long bookId = cart.getBookId();
        BookResponse bookResponse = bookService.getBookById(bookId).getBody();
        if (cart.getQuantity() > bookResponse.getQuantity()) {
            throw new InvalidQuantityException("Not enough stock for book: " + bookResponse.getBookName()+ " in cart " + cart.getCartId());
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDate.now());
        order.setPrice(cart.getTotalPrice());
        order.setAddress(orderRequest.getAddress());
        order.setCancel(false);
        order.setQuantity(cart.getQuantity());
        OrderBook orderBook = new OrderBook();
        orderBook.setOrder(order);
        orderBook.setBookId(bookId);
        orderBook.setQuantity(cart.getQuantity());
        order.setOrderBooks(List.of(orderBook));

        bookResponse.setQuantity(bookResponse.getQuantity() - cart.getQuantity());
        bookService.changeBookQuantity(bookId, bookResponse.getQuantity());

        order = orderRepo.save(order);

        cartService.removeFromCartByCartId(cartId);
        return orderMapper.mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundByIdException("Order not found"));

        if (order.isCancel()) {
            throw new RuntimeException("Order is already canceled");
        }

        order.getOrderBooks().forEach(orderBook -> {
            Long bookId = orderBook.getBookId();
            BookResponse bookResponse = bookService.getBookById(bookId).getBody();
            bookResponse.setQuantity(bookResponse.getQuantity() + orderBook.getQuantity());
            bookService.changeBookQuantity(bookId, bookResponse.getQuantity());
        });

        order.setCancel(true);
        orderRepo.save(order);

        return orderMapper.mapToOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAll().stream()
                .map(orderMapper::mapToOrderResponse)
                .toList();
    }


    public List<OrderResponse> getAllOrdersForUser(Long userId) {
        return orderRepo.findByUserId(userId).stream()
                .map(orderMapper::mapToOrderResponse)
                .toList();
    }

}
