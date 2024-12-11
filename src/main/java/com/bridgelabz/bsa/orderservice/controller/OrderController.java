package com.bridgelabz.bsa.orderservice.controller;


import com.bridgelabz.bsa.orderservice.requestdto.OrderRequest;
import com.bridgelabz.bsa.orderservice.responsedto.OrderResponse;
import com.bridgelabz.bsa.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrderByUser(@RequestHeader("userId") String authHeader, @RequestBody @Valid OrderRequest orderRequest) {
        Long userId = Long.valueOf(authHeader);
        OrderResponse orderResponse = orderService.placeOrderByUser(userId, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @PostMapping("/orders/place/{cartId}")
    public ResponseEntity<OrderResponse> placeOrderByCartId(@RequestHeader("userId") String authHeader,@PathVariable long cartId,@Valid @RequestBody OrderRequest orderRequest) {
        Long userId = Long.valueOf(authHeader);
        OrderResponse orderResponse = orderService.placeOrderByCartId(userId, cartId, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@RequestHeader("userId") String authHeader, @PathVariable long orderId) {
        OrderResponse orderResponse = orderService.cancelOrder(orderId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderResponse);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orderResponses = orderService.getAllOrders();
        return ResponseEntity.status(HttpStatus.OK).body(orderResponses);
    }

    @GetMapping("/orders/user")
    public ResponseEntity<List<OrderResponse>> getAllOrdersForUser(@RequestHeader("userId") String authHeader) {
        Long userId = Long.valueOf(authHeader);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrdersForUser(userId));
    }


}
