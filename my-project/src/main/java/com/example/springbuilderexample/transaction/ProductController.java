package com.example.springbuilderexample.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/success")
    public ResponseEntity<String> createProductsSuccessfully() {
        productService.createProductsSuccessfully();
        return ResponseEntity.ok("Successfully created 2 products.");
    }

    @PostMapping("/rollback")
    public ResponseEntity<String> createProductsWithRollback() {
        try {
            productService.createProductsWithRollback();
        } catch (Exception e) {
            // The exception is caught here so the controller can return a response.
            // The transaction will still be rolled back by Spring.
            return ResponseEntity.status(500).body("Transaction rolled back. Check the console and the database.");
        }
        return ResponseEntity.ok("This should not be reached.");
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }
}
