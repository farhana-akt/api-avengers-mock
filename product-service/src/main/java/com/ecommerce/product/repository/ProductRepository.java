package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Repository
 *
 * Data access layer for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all active products
     */
    List<Product> findByActiveTrue();

    /**
     * Find products by category
     */
    List<Product> findByCategoryAndActiveTrue(String category);

    /**
     * Find products by name containing (case-insensitive search)
     */
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    /**
     * Find products by brand
     */
    List<Product> findByBrandAndActiveTrue(String brand);
}
