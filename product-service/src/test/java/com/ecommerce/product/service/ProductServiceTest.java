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

    @Test
    void testGetProductsByBrand_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByBrandAndActiveTrue("TestBrand")).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getProductsByBrand("TestBrand");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("TestBrand", response.get(0).getBrand());
        verify(productRepository, times(1)).findByBrandAndActiveTrue("TestBrand");
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
