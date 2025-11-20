package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for ProductService using Mockito
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory("Electronics");
        testProduct.setBrand("TestBrand");
        testProduct.setImageUrl("http://example.com/image.jpg");
        testProduct.setActive(true);

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setCategory("Electronics");
        productRequest.setBrand("TestBrand");
        productRequest.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct, createProduct(2L, "Product 2"));
        when(productRepository.findByActiveTrue()).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getAllProducts();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Test Product", response.get(0).getName());
        verify(productRepository, times(1)).findByActiveTrue();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> response = productService.getAllProducts();

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(productRepository, times(1)).findByActiveTrue();
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponse response = productService.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testProduct.getId(), response.getId());
        assertEquals(testProduct.getName(), response.getName());
        assertEquals(testProduct.getPrice(), response.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(999L);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testGetProductsByCategory_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryAndActiveTrue("Electronics")).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Electronics", response.get(0).getCategory());
        verify(productRepository, times(1)).findByCategoryAndActiveTrue("Electronics");
    }

    @Test
    void testGetProductsByCategory_NoResults() {
        // Arrange
        when(productRepository.findByCategoryAndActiveTrue("NonExistent")).thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> response = productService.getProductsByCategory("NonExistent");

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(productRepository, times(1)).findByCategoryAndActiveTrue("NonExistent");
    }

    @Test
    void testSearchProducts_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCaseAndActiveTrue("Test")).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.searchProducts("Test");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.get(0).getName().contains("Test"));
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndActiveTrue("Test");
    }

    @Test
    void testSearchProducts_NoResults() {
        // Arrange
        when(productRepository.findByNameContainingIgnoreCaseAndActiveTrue("NonExistent")).thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> response = productService.searchProducts("NonExistent");

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(productRepository, times(1)).findByNameContainingIgnoreCaseAndActiveTrue("NonExistent");
    }

    @Test
    void testCreateProduct_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductResponse response = productService.createProduct(productRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testProduct.getName(), response.getName());
        assertEquals(testProduct.getPrice(), response.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productRequest.setName("Updated Product");
        productRequest.setPrice(new BigDecimal("149.99"));

        // Act
        ProductResponse response = productService.updateProduct(1L, productRequest);

        // Assert
        assertNotNull(response);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(999L, productRequest);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(999L);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // ========== Tests for New Advanced Search Features ==========

    @Test
    void testGetProductsByBrand_Success() {
        // Arrange
        Product product1 = createProduct(1L, "Sony TV");
        product1.setBrand("Sony");
        Product product2 = createProduct(2L, "Sony Speaker");
        product2.setBrand("Sony");
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findByBrandAndActiveTrue("Sony")).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getProductsByBrand("Sony");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Sony", response.get(0).getBrand());
        assertEquals("Sony", response.get(1).getBrand());
        verify(productRepository, times(1)).findByBrandAndActiveTrue("Sony");
    }

    @Test
    void testGetProductsByBrand_NoResults() {
        // Arrange
        when(productRepository.findByBrandAndActiveTrue("UnknownBrand")).thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> response = productService.getProductsByBrand("UnknownBrand");

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(productRepository, times(1)).findByBrandAndActiveTrue("UnknownBrand");
    }

    @Test
    void testGetProductsByPriceRange_Success() {
        // Arrange
        Product product1 = createProduct(1L, "Budget Phone");
        product1.setPrice(new BigDecimal("200.00"));
        Product product2 = createProduct(2L, "Mid Range Phone");
        product2.setPrice(new BigDecimal("400.00"));
        List<Product> products = Arrays.asList(product1, product2);

        BigDecimal minPrice = new BigDecimal("100.00");
        BigDecimal maxPrice = new BigDecimal("500.00");
        when(productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice)).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(productRepository, times(1)).findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
    }

    @Test
    void testGetProductsByPriceRange_InvalidNegativePrice() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("-10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductsByPriceRange(minPrice, maxPrice);
        });

        assertTrue(exception.getMessage().contains("non-negative"));
        verify(productRepository, never()).findByPriceBetweenAndActiveTrue(any(), any());
    }

    @Test
    void testGetProductsByPriceRange_MinGreaterThanMax() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("500.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductsByPriceRange(minPrice, maxPrice);
        });

        assertTrue(exception.getMessage().contains("Minimum price cannot be greater than maximum price"));
        verify(productRepository, never()).findByPriceBetweenAndActiveTrue(any(), any());
    }

    @Test
    void testGetProductsByPriceRange_NoResults() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("1000.00");
        BigDecimal maxPrice = new BigDecimal("2000.00");
        when(productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice)).thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> response = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(productRepository, times(1)).findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
    }

    @Test
    void testGetProductsByCategoryAndPriceRange_Success() {
        // Arrange
        Product product1 = createProduct(1L, "Gaming Laptop");
        product1.setCategory("Electronics");
        product1.setPrice(new BigDecimal("800.00"));
        List<Product> products = Arrays.asList(product1);

        BigDecimal minPrice = new BigDecimal("500.00");
        BigDecimal maxPrice = new BigDecimal("1000.00");
        when(productRepository.findByCategoryAndPriceBetweenAndActiveTrue("Electronics", minPrice, maxPrice))
            .thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getProductsByCategoryAndPriceRange(
            "Electronics", minPrice, maxPrice
        );

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Electronics", response.get(0).getCategory());
        verify(productRepository, times(1))
            .findByCategoryAndPriceBetweenAndActiveTrue("Electronics", minPrice, maxPrice);
    }

    @Test
    void testGetProductsByCategoryAndPriceRange_InvalidPrice() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("-50.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductsByCategoryAndPriceRange("Electronics", minPrice, maxPrice);
        });

        assertTrue(exception.getMessage().contains("non-negative"));
        verify(productRepository, never()).findByCategoryAndPriceBetweenAndActiveTrue(any(), any(), any());
    }

    @Test
    void testSearchWithFilters_AllParameters() {
        // Arrange
        Product product = createProduct(1L, "Sony TV");
        product.setCategory("Electronics");
        product.setBrand("Sony");
        product.setPrice(new BigDecimal("300.00"));
        List<Product> products = Arrays.asList(product);

        String category = "Electronics";
        String brand = "Sony";
        BigDecimal minPrice = new BigDecimal("200.00");
        BigDecimal maxPrice = new BigDecimal("500.00");

        when(productRepository.findByFilters(category, brand, minPrice, maxPrice)).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.searchWithFilters(category, brand, minPrice, maxPrice);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Electronics", response.get(0).getCategory());
        assertEquals("Sony", response.get(0).getBrand());
        verify(productRepository, times(1)).findByFilters(category, brand, minPrice, maxPrice);
    }

    @Test
    void testSearchWithFilters_OnlyCategoryAndBrand() {
        // Arrange
        Product product = createProduct(1L, "Samsung Phone");
        product.setCategory("Electronics");
        product.setBrand("Samsung");
        List<Product> products = Arrays.asList(product);

        String category = "Electronics";
        String brand = "Samsung";

        when(productRepository.findByFilters(category, brand, null, null)).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.searchWithFilters(category, brand, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(productRepository, times(1)).findByFilters(category, brand, null, null);
    }

    @Test
    void testSearchWithFilters_OnlyPriceRange() {
        // Arrange
        Product product = createProduct(1L, "Budget Item");
        product.setPrice(new BigDecimal("50.00"));
        List<Product> products = Arrays.asList(product);

        BigDecimal minPrice = new BigDecimal("20.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        when(productRepository.findByFilters(null, null, minPrice, maxPrice)).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.searchWithFilters(null, null, minPrice, maxPrice);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(productRepository, times(1)).findByFilters(null, null, minPrice, maxPrice);
    }

    @Test
    void testSearchWithFilters_InvalidPriceRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("500.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.searchWithFilters("Electronics", "Sony", minPrice, maxPrice);
        });

        assertTrue(exception.getMessage().contains("Minimum price cannot be greater than maximum price"));
        verify(productRepository, never()).findByFilters(any(), any(), any(), any());
    }

    @Test
    void testSearchWithFilters_NegativePrice() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("-10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.searchWithFilters(null, null, minPrice, maxPrice);
        });

        assertTrue(exception.getMessage().contains("non-negative"));
        verify(productRepository, never()).findByFilters(any(), any(), any(), any());
    }

    @Test
    void testSearchWithFilters_NoResults() {
        // Arrange
        when(productRepository.findByFilters("NonExistent", "UnknownBrand", null, null))
            .thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> response = productService.searchWithFilters("NonExistent", "UnknownBrand", null, null);

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(productRepository, times(1)).findByFilters("NonExistent", "UnknownBrand", null, null);
    }

    @Test
    void testSearchWithFilters_AllParametersNull() {
        // Arrange
        List<Product> allProducts = Arrays.asList(
            createProduct(1L, "Product 1"),
            createProduct(2L, "Product 2"),
            createProduct(3L, "Product 3")
        );
        when(productRepository.findByFilters(null, null, null, null)).thenReturn(allProducts);

        // Act
        List<ProductResponse> response = productService.searchWithFilters(null, null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.size());
        verify(productRepository, times(1)).findByFilters(null, null, null, null);
    }

    // Helper method to create test products
    private Product createProduct(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Description for " + name);
        product.setPrice(new BigDecimal("79.99"));
        product.setCategory("Electronics");
        product.setBrand("TestBrand");
        product.setActive(true);
        return product;
    }
}
