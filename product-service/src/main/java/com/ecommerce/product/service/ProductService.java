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

import java.math.BigDecimal;
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

    /**
     * Get products by brand
     */
    public List<ProductResponse> getProductsByBrand(String brand) {
        logger.info("Fetching products for brand: {}", brand);
        return productRepository.findByBrandAndActiveTrue(brand)
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get products within a price range
     */
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Fetching products with price between {} and {}", minPrice, maxPrice);

        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price values must be non-negative");
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice)
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get products by category within a price range
     */
    public List<ProductResponse> getProductsByCategoryAndPriceRange(
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice
    ) {
        logger.info("Fetching products in category '{}' with price between {} and {}",
            category, minPrice, maxPrice);

        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price values must be non-negative");
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        return productRepository.findByCategoryAndPriceBetweenAndActiveTrue(
            category, minPrice, maxPrice
        )
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Advanced search with multiple optional filters
     * All parameters are optional (can be null)
     */
    public List<ProductResponse> searchWithFilters(
        String category,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice
    ) {
        logger.info("Searching products with filters - category: {}, brand: {}, minPrice: {}, maxPrice: {}",
            category, brand, minPrice, maxPrice);

        // Validate price range if both prices are provided
        if (minPrice != null && maxPrice != null) {
            if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price values must be non-negative");
            }

            if (minPrice.compareTo(maxPrice) > 0) {
                throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
            }
        }

        return productRepository.findByFilters(category, brand, minPrice, maxPrice)
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }
}
