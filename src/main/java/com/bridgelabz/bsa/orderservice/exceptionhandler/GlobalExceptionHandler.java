package com.bridgelabz.bsa.orderservice.exceptionhandler;

import com.bridgelabz.bsa.orderservice.exception.InvalidQuantityException;
import com.bridgelabz.bsa.orderservice.exception.OrderNotFoundByIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(OrderNotFoundByIdException.class)
    public ResponseEntity<String> handleOrderNotFoundById(OrderNotFoundByIdException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<String> handleInvalidQuantity(InvalidQuantityException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
