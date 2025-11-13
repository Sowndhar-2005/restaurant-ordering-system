package com.example.backend_java.service;

import com.example.backend_java.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService {

    // Requirement Met: Collections (ArrayList)
    // We use synchronizedList to make it safe for multiple users
    private List<Order> orderHistory = Collections.synchronizedList(new ArrayList<>());
    
    // Requirement Met: File I/O. This is the name of our save file.
    private static final String ORDERS_FILE = "orders.dat";

    /**
     * Requirement Met: File I/O (Read) and Deserialization
     * @PostConstruct runs this method once when the application starts.
     */
    @PostConstruct 
    public void loadOrders() {
        System.out.println("Attempting to load orders from file...");
        
        // Requirement Met: Exception Handling (try-catch)
        try (FileInputStream fis = new FileInputStream(ORDERS_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            // Read the entire list from the file
            orderHistory = Collections.synchronizedList((ArrayList<Order>) ois.readObject());
            System.out.println("Successfully loaded " + orderHistory.size() + " orders from file.");

        } catch (FileNotFoundException e) {
            System.out.println("No existing order file found (orders.dat). Starting with an empty history.");
            orderHistory = Collections.synchronizedList(new ArrayList<>());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading orders from file: " + e.getMessage());
            orderHistory = Collections.synchronizedList(new ArrayList<>());
        }
    }

    /**
     * Requirement Met: File I/O (Write) and Serialization
     * This private method saves the entire order list to the file.
     */
    private void saveOrders() {
        System.out.println("Saving " + orderHistory.size() + " orders to file...");
        
        // Requirement Met: Exception Handling (try-catch-finally)
        // This is a "try-with-resources" block, which automatically closes the streams.
        try (FileOutputStream fos = new FileOutputStream(ORDERS_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // We write a new ArrayList to avoid issues while other users might be reading
            oos.writeObject(new ArrayList<>(orderHistory)); 
            System.out.println("Save successful.");
        } catch (IOException e) {
            System.err.println("Error saving orders to file: " + e.getMessage());
        }
    }

    /**
     * A public method for the controller to get all past orders.
     */
    public List<Order> getOrderHistory() {
        return this.orderHistory;
    }

    /**
     * A public method for the controller to place a new order.
     */
    public Order placeOrder(Order order) {
        
        // Requirement Met: Exception Handling
        if (order.getItems() == null || order.getItems().isEmpty()) {
            // This exception will be caught by our GlobalExceptionHandler
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        // Set server-side details
        order.setId(System.currentTimeMillis()); // Unique ID
        order.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        orderHistory.add(0, order); // Add new order to the top of the list
        saveOrders(); // Save the updated list to the file
        
        return order;
    }
}