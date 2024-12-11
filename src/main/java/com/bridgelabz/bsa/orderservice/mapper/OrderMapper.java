package com.bridgelabz.bsa.orderservice.mapper;


import com.bridgelabz.bsa.orderservice.model.Order;
import com.bridgelabz.bsa.orderservice.responsedto.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse mapToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setUserId(order.getUserId());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setPrice(order.getPrice());
        orderResponse.setAddress(order.getAddress());
        orderResponse.setQuantity(order.getQuantity());
        return orderResponse;
    }
}
