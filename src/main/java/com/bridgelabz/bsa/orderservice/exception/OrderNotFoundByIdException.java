package com.bridgelabz.bsa.orderservice.exception;

public class OrderNotFoundByIdException extends RuntimeException {

    private String message;

    public OrderNotFoundByIdException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
