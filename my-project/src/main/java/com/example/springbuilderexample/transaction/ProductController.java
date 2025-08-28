package com.example.springbuilderexample.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
            return ResponseEntity.status(500).body("Transaction rolled back as expected. Check the database.");
        }
        return ResponseEntity.ok("This should not be reached.");
    }

    @PostMapping("/requires-new")
    public ResponseEntity<String> createProductsWithRequiresNew() {
        try {
            productService.outerRequiresNew();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Outer transaction rolled back, but inner transaction (REQUIRES_NEW) was committed. Check the database.");
        }
        return ResponseEntity.ok("This should not be reached.");
    }

    @PostMapping("/checked-exception")
    public ResponseEntity<String> createProductWithCheckedException() {
        try {
            productService.createProductWithCheckedException();
        } catch (CustomCheckedException e) {
            return ResponseEntity.status(500).body("Transaction rolled back for checked exception as expected. Check the database.");
        }
        return ResponseEntity.ok("This should not be reached.");
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.ok("All products deleted.");
    }
}
