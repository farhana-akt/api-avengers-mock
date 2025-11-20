package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product Controller
 *
 * REST API endpoints for product management.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * Get all products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            logger.error("Failed to get product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Search products
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
        List<ProductResponse> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    /**
     * Create a new product (admin only - requires authentication)
     */
    @PostMapping
    public ResponseEntity<?> createProduct(
        @Valid @RequestBody ProductRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        try {
            // Check if user is admin (this header is set by API Gateway after JWT validation)
            if (userRole == null || !userRole.equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorResponse("Only administrators can create products"));
            }

            ProductResponse product = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            logger.error("Failed to create product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Update a product (admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
        @PathVariable Long id,
        @Valid @RequestBody ProductRequest request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        try {
            // Check if user is admin
            if (userRole == null || !userRole.equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorResponse("Only administrators can update products"));
            }

            ProductResponse product = productService.updateProduct(id, request);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            logger.error("Failed to update product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a product (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        try {
            // Check if user is admin
            if (userRole == null || !userRole.equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorResponse("Only administrators can delete products"));
            }

            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to delete product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get products by brand
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(@PathVariable String brand) {
        List<ProductResponse> products = productService.getProductsByBrand(brand);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by price range
     * Example: /api/products/price-range?min=10.00&max=100.00
     */
    @GetMapping("/price-range")
    public ResponseEntity<?> getProductsByPriceRange(
        @RequestParam BigDecimal min,
        @RequestParam BigDecimal max
    ) {
        try {
            List<ProductResponse> products = productService.getProductsByPriceRange(min, max);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid price range: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get products by category and price range
     * Example: /api/products/filter/Electronics?min=100&max=500
     */
    @GetMapping("/filter/{category}")
    public ResponseEntity<?> getProductsByCategoryAndPriceRange(
        @PathVariable String category,
        @RequestParam BigDecimal min,
        @RequestParam BigDecimal max
    ) {
        try {
            List<ProductResponse> products = productService.getProductsByCategoryAndPriceRange(
                category, min, max
            );
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid filter parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Advanced search with multiple optional filters
     * Example: /api/products/advanced-search?category=Electronics&brand=Sony&minPrice=100&maxPrice=500
     * All parameters are optional
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<?> advancedSearch(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice
    ) {
        try {
            List<ProductResponse> products = productService.searchWithFilters(
                category, brand, minPrice, maxPrice
            );
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid search parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "product-service");
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create error response
     */
    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
