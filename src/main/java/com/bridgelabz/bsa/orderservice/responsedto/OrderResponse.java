package com.bridgelabz.bsa.orderservice.responsedto;

import com.bridgelabz.bsa.orderservice.model.Address;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderResponse {

    private long orderId;
    private LocalDate orderDate;
    private double price;
    private int quantity;
    private long userId;
    private Address address;
}
