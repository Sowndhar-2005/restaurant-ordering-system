package com.example.backend_java.controller;

import com.example.backend_java.model.Category;
import com.example.backend_java.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono; // Import Mono

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*") 
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // The method now returns a Mono<List<Category>>
    @GetMapping
    public Mono<List<Category>> getMenu() {
        return menuService.getMenu();
    }
}