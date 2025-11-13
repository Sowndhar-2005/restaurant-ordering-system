package com.example.backend_java.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // This is similar to MenuItem, but includes quantity
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;
}