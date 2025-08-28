package com.example.springbuilderexample.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Inject ProductService to call its own methods with transaction proxy
    @Autowired
    private ProductService self;

    /**
     * This method demonstrates a successful transaction.
     * The @Transactional annotation ensures that this method is executed within a single transaction.
     */
    @Transactional
    public void createProductsSuccessfully() {
        productRepository.save(new Product("Book"));
        productRepository.save(new Product("Pen"));
    }

    /**
     * This method demonstrates a transaction that will be rolled back due to a RuntimeException.
     * The entire operation is rolled back, so "Laptop" will not be saved.
     */
    @Transactional
    public void createProductsWithRollback() {
        productRepository.save(new Product("Laptop"));
        if (true) {
            throw new RuntimeException("Simulating an error during transaction!");
        }
        productRepository.save(new Product("Mouse"));
    }

    /**
     * This method demonstrates Propagation.REQUIRES_NEW.
     * It starts its own transaction. It calls an inner method that starts another, independent transaction.
     * The outer transaction will be rolled back, but the inner transaction will be committed.
     */
    @Transactional
    public void outerRequiresNew() {
        productRepository.save(new Product("Outer Product")); // This will be rolled back

        try {
            self.innerRequiresNew(); // This will commit
        } catch (RuntimeException e) {
            // Log the exception, but the inner transaction is already committed
            System.out.println("Caught exception from inner transaction: " + e.getMessage());
        }

        throw new RuntimeException("Rollback outer transaction");
    }

    /**
     * This inner method is annotated with Propagation.REQUIRES_NEW.
     * It suspends the current transaction (if one exists), and starts a new one.
     * This new transaction is independent of the outer transaction.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerRequiresNew() {
        productRepository.save(new Product("Inner Product")); // This will be committed
    }

    /**
     * This method demonstrates a transaction that rolls back for a checked exception.
     * By default, @Transactional only rolls back for RuntimeExceptions and Errors.
     * We use `rollbackFor` to change this behavior.
     */
    @Transactional(rollbackFor = CustomCheckedException.class)
    public void createProductWithCheckedException() throws CustomCheckedException {
        productRepository.save(new Product("Checked Exception Product"));
        throw new CustomCheckedException("This is a checked exception that should cause a rollback.");
    }

    /**
     * This method demonstrates a read-only transaction.
     * This is an optimization hint for the persistence provider.
     */
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    // A non-transactional method to clear the repository for demonstration purposes.
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }
}
