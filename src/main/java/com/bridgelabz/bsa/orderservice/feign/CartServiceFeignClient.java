package com.bridgelabz.bsa.orderservice.feign;

import com.bridgelabz.bsa.orderservice.responsedto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("CART-SERVICE")
public interface CartServiceFeignClient {

    @GetMapping("/carts/user")
    public ResponseEntity<List<CartResponse>> getAllCartItemsForUser(@RequestHeader("userId") String authHeader);

    @DeleteMapping("/carts/user/remove-all")
    public ResponseEntity<List<CartResponse>> removeFromCartByUserId(@RequestHeader("userId") String authHeader);

    @DeleteMapping("/carts/{cartId}/remove")
    public ResponseEntity<CartResponse> removeFromCartByCartId(@PathVariable long cartId);

    @GetMapping("/carts/{cartId}")
    public ResponseEntity<CartResponse> getCartByCartId(@PathVariable long cartId);
}
