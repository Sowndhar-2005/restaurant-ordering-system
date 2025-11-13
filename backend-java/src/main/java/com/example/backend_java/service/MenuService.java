package com.example.backend_java.service;

import com.example.backend_java.model.ApiMenuItem;
import com.example.backend_java.model.Category;
import com.example.backend_java.model.MenuItem;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final WebClient webClient;

    // List of category endpoints to fetch
    private final List<String> categories = List.of(
            "bbqs", "best-foods", "breads", "burgers", "chocolates", "desserts",
            "drinks", "fried-chicken", "ice-cream", "pizzas", "porks",
            "sandwiches", "sausages", "steaks"
    );

    public MenuService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://free-food-menus-api-two.vercel.app").build();
    }

    public Mono<List<Category>> getMenu() {
        // Flux.fromIterable() creates an asynchronous stream from our list of categories
        return Flux.fromIterable(categories)
                // flatMap runs an API call for each category name in parallel
                .flatMap(categoryName -> 
                    this.webClient.get()                // Start a GET request
                        .uri("/" + categoryName)        // Set the URL (e.g., /bbqs)
                        .retrieve()                     // Get the response
                        .bodyToFlux(ApiMenuItem.class)  // Convert the JSON response to a stream of ApiMenuItem
                        .collectList()                  // Collect all items in the stream into a List<ApiMenuItem>
                        .map(apiItems ->                 // Convert this list into our own Category object
                            this.mapToCategory(categoryName, apiItems)
                        )
                )
                .collectList(); // Finally, collect all Category objects into a single List<Category>
    }

    private MenuItem mapApiMenuItemToMenuItem(ApiMenuItem apiItem) {
        MenuItem item = new MenuItem();
        item.setId(apiItem.getId());
        item.setName(apiItem.getName());
        item.setDescription(apiItem.getDsc()); // Map 'dsc' to 'description'
        item.setPrice(apiItem.getPrice());
        item.setImageUrl(apiItem.getImg());    // Map 'img' to 'imageUrl'
        return item;
    }

    private Category mapToCategory(String categoryName, List<ApiMenuItem> apiItems) {
        Category category = new Category();
        category.setName(this.formatCategoryName(categoryName)); // Use the fixed method
        category.setItems(
            apiItems.stream()
                .map(this::mapApiMenuItemToMenuItem) // Convert each ApiMenuItem to a MenuItem
                .collect(Collectors.toList()) // Use Collectors.toList()
        );
        return category;
    }

    // --- THIS IS THE FIXED JAVA METHOD ---
    private String formatCategoryName(String name) {
        // Replaces dashes with spaces
        String replacedName = name.replace("-", " ");
        
        // Capitalizes the first letter of each word
        String[] words = replacedName.split(" ");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                String firstLetter = word.substring(0, 1).toUpperCase();
                String restOfWord = word.substring(1);
                capitalizedWords.append(firstLetter).append(restOfWord).append(" ");
            }
        }
        return capitalizedWords.toString().trim(); // Trim trailing space
    }
}