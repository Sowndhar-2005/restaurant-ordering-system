package com.example.backend_java.model;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
// This tells Lombok to include the fields from Person in its methods
@EqualsAndHashCode(callSuper = true) 
public class Customer extends Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String phoneNumber;
}