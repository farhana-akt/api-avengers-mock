package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    /**
     * Find active products within a price range
     */
    List<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find active products by category within a price range
     */
    List<Product> findByCategoryAndPriceBetweenAndActiveTrue(
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice
    );

    /**
     * Advanced search: filter by multiple criteria
     * Supports optional category, brand, and price range filtering
     */
    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:brand IS NULL OR p.brand = :brand) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> findByFilters(
        @Param("category") String category,
        @Param("brand") String brand,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
}
