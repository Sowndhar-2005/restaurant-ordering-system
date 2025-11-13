package com.example.backend_java.model;

import java.io.Serializable;
import java.util.List; // Make sure to import java.util.List
import lombok.Data;

@Data
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    // A Category holds a list of MenuItem objects
    private List<MenuItem> items; 
}