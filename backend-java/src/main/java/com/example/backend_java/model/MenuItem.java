package com.example.backend_java.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
}