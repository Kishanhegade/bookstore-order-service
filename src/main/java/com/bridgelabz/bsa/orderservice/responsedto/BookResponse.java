package com.bridgelabz.bsa.orderservice.responsedto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponse {

    private Integer bookId;
    private String bookName;
    private String authorName;
    private String description;
    private Double price;
    private int quantity;
}
