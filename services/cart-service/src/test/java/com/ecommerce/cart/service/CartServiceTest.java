package com.ecommerce.cart.service;

import com.ecommerce.cart.model.AddToCartRequest;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for CartService using Mockito
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private AddToCartRequest addToCartRequest;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        testCart = new Cart();
        testCart.setUserId(userId);

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(101L);
        addToCartRequest.setProductName("Test Product");
        addToCartRequest.setPrice(new BigDecimal("99.99"));
        addToCartRequest.setQuantity(2);
    }

    @Test
    void testGetCart_ExistingCart() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.getCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(valueOperations, times(1)).get("cart:" + userId);
    }

    @Test
    void testGetCart_NewCart() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act
        Cart result = cartService.getCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.getItems().isEmpty());
        verify(valueOperations, times(1)).get("cart:" + userId);
    }

    @Test
    void testAddToCart_NewItem() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.addToCart(userId, addToCartRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem item = result.getItems().get(0);
        assertEquals(addToCartRequest.getProductId(), item.getProductId());
        assertEquals(addToCartRequest.getQuantity(), item.getQuantity());
        verify(valueOperations, times(1)).set(eq("cart:" + userId), any(Cart.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testAddToCart_ExistingItem() {
        // Arrange
        CartItem existingItem = new CartItem(101L, "Test Product", new BigDecimal("99.99"), 3);
        testCart.getItems().add(existingItem);
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.addToCart(userId, addToCartRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem item = result.getItems().get(0);
        assertEquals(101L, item.getProductId());
        assertEquals(5, item.getQuantity()); // 3 + 2
        verify(valueOperations, times(1)).set(eq("cart:" + userId), any(Cart.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testRemoveFromCart_Success() {
        // Arrange
        CartItem item1 = new CartItem(101L, "Product 1", new BigDecimal("99.99"), 2);
        CartItem item2 = new CartItem(102L, "Product 2", new BigDecimal("49.99"), 1);
        testCart.getItems().add(item1);
        testCart.getItems().add(item2);
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.removeFromCart(userId, 101L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(102L, result.getItems().get(0).getProductId());
        verify(valueOperations, times(1)).set(eq("cart:" + userId), any(Cart.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testRemoveFromCart_NonExistentItem() {
        // Arrange
        CartItem item1 = new CartItem(101L, "Product 1", new BigDecimal("99.99"), 2);
        testCart.getItems().add(item1);
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.removeFromCart(userId, 999L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size()); // No change
        assertEquals(101L, result.getItems().get(0).getProductId());
    }

    @Test
    void testUpdateQuantity_Success() {
        // Arrange
        CartItem item = new CartItem(101L, "Product 1", new BigDecimal("99.99"), 2);
        testCart.getItems().add(item);
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.updateQuantity(userId, 101L, 5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(valueOperations, times(1)).set(eq("cart:" + userId), any(Cart.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testUpdateQuantity_RemoveWhenZero() {
        // Arrange
        CartItem item = new CartItem(101L, "Product 1", new BigDecimal("99.99"), 2);
        testCart.getItems().add(item);
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.updateQuantity(userId, 101L, 0);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(valueOperations, times(1)).set(eq("cart:" + userId), any(Cart.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testUpdateQuantity_RemoveWhenNegative() {
        // Arrange
        CartItem item = new CartItem(101L, "Product 1", new BigDecimal("99.99"), 2);
        testCart.getItems().add(item);
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act
        Cart result = cartService.updateQuantity(userId, 101L, -1);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testUpdateQuantity_ProductNotFound() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(testCart);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateQuantity(userId, 999L, 5);
        });

        assertEquals("Product not found in cart", exception.getMessage());
    }

    @Test
    void testClearCart_Success() {
        // Arrange
        CartItem item = new CartItem(101L, "Product 1", new BigDecimal("99.99"), 2);
        testCart.getItems().add(item);

        // Act
        cartService.clearCart(userId);

        // Assert
        verify(redisTemplate, times(1)).delete("cart:" + userId);
    }

    @Test
    void testCartTotalPrice() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn(testCart);
        AddToCartRequest request1 = new AddToCartRequest();
        request1.setProductId(101L);
        request1.setProductName("Product 1");
        request1.setPrice(new BigDecimal("100.00"));
        request1.setQuantity(2);

        AddToCartRequest request2 = new AddToCartRequest();
        request2.setProductId(102L);
        request2.setProductName("Product 2");
        request2.setPrice(new BigDecimal("50.00"));
        request2.setQuantity(3);

        // Act
        cartService.addToCart(userId, request1);
        cartService.addToCart(userId, request2);

        // Assert
        verify(valueOperations, times(2)).set(eq("cart:" + userId), any(Cart.class), eq(24L), eq(TimeUnit.HOURS));
    }
}
