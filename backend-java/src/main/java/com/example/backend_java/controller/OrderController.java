package com.example.backend_java.controller;

import com.example.backend_java.model.Order;
import com.example.backend_java.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders") // Base URL is /api/orders
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // This method handles GET /api/orders
    @GetMapping
    public List<Order> getOrderHistory() {
        return orderService.getOrderHistory();
    }

    // This method handles POST /api/orders
    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        // @RequestBody tells Spring to turn the JSON from the frontend
        // into an "Order" object
        return orderService.placeOrder(order);
    }
}