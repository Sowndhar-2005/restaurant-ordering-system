package com.example.backend_java.model;

import java.io.Serializable;
import lombok.Data;

// This class matches the JSON from the public API
@Data
public class ApiMenuItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String dsc; // The API uses 'dsc' for description
    private double price;
    private String img; // The API uses 'img' for imageUrl
    private int rate;
    private String country;
}