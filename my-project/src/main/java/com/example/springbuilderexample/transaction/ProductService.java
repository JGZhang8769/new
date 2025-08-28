package com.example.springbuilderexample.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * This method demonstrates a successful transaction.
     * All products in the list will be saved to the database.
     * The @Transactional annotation ensures that this method is executed within a single transaction.
     */
    @Transactional
    public void createProductsSuccessfully() {
        productRepository.save(new Product("Book"));
        productRepository.save(new Product("Pen"));
    }

    /**
     * This method demonstrates a transaction that will be rolled back.
     * It saves one product, then throws a runtime exception.
     * Because of the @Transactional annotation, the entire operation is rolled back,
     * so the first product ("Laptop") will not be saved in the database.
     */
    @Transactional
    public void createProductsWithRollback() {
        productRepository.save(new Product("Laptop"));
        if (true) {
            throw new RuntimeException("Simulating an error during transaction!");
        }
        productRepository.save(new Product("Mouse"));
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
}
