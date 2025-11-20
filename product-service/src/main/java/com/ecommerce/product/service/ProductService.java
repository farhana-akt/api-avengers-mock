package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service
 *
 * Business logic for product management.
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get all active products
     */
    public List<ProductResponse> getAllProducts() {
        logger.info("Fetching all active products");
        return productRepository.findByActiveTrue()
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    public ProductResponse getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        return ProductResponse.fromEntity(product);
    }

    /**
     * Get products by category
     */
    public List<ProductResponse> getProductsByCategory(String category) {
        logger.info("Fetching products in category: {}", category);
        return productRepository.findByCategoryAndActiveTrue(category)
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Search products by name
     */
    public List<ProductResponse> searchProducts(String query) {
        logger.info("Searching products with query: {}", query);
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(query)
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Create a new product
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        logger.info("Creating new product: {}", request.getName());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setActive(true);

        product = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", product.getId());

        return ProductResponse.fromEntity(product);
    }

    /**
     * Update an existing product
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        logger.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());

        product = productRepository.save(product);
        logger.info("Product updated successfully: {}", product.getId());

        return ProductResponse.fromEntity(product);
    }

    /**
     * Delete a product (soft delete)
     */
    @Transactional
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        product.setActive(false);
        productRepository.save(product);

        logger.info("Product deleted successfully: {}", id);
    }
}
