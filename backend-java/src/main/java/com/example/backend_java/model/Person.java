package com.example.backend_java.model;

import java.io.Serializable;
import lombok.Data;

// @Data creates getters, setters, and toString() for you
@Data
public class Person implements Serializable {
    // serialVersionUID is needed for Serializable
    private static final long serialVersionUID = 1L; 
    
    protected String name;
    protected String email;
}