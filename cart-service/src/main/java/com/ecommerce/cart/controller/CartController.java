package com.ecommerce.cart.controller;

import com.ecommerce.cart.model.AddToCartRequest;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Cart Controller
 *
 * REST API endpoints for shopping cart operations.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /**
     * Get cart for the authenticated user
     * The X-User-Id header is added by the API Gateway after JWT validation
     */
    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader("X-User-Id") String userId) {
        try {
            Cart cart = cartService.getCart(Long.parseLong(userId));
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Failed to get cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Add item to cart
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody AddToCartRequest request
    ) {
        try {
            Cart cart = cartService.addToCart(Long.parseLong(userId), request);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Failed to add to cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable Long productId
    ) {
        try {
            Cart cart = cartService.removeFromCart(Long.parseLong(userId), productId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Failed to remove from cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Update item quantity in cart
     */
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateQuantity(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable Long productId,
        @RequestParam Integer quantity
    ) {
        try {
            Cart cart = cartService.updateQuantity(Long.parseLong(userId), productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            logger.error("Failed to update cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Clear cart
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestHeader("X-User-Id") String userId) {
        try {
            cartService.clearCart(Long.parseLong(userId));

            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to clear cart: {}", e.getMessage());
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
        response.put("service", "cart-service");
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
