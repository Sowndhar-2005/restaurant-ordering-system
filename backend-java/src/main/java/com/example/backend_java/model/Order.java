package com.example.backend_java.model;

import java.io.Serializable;
import java.util.List; // Make sure to import java.util.List
import lombok.Data;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String date;
    private double total;
    
    // An Order holds a list of CartItem objects
    private List<CartItem> items;
    // An Order is placed by a Customer
    private Customer customer;
}