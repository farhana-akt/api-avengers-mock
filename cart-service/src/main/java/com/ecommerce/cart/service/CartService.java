package com.ecommerce.cart.service;

import com.ecommerce.cart.model.AddToCartRequest;
import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Cart Service
 *
 * Business logic for shopping cart operations using Redis.
 */
@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private static final String CART_KEY_PREFIX = "cart:";
    private static final long CART_TTL_HOURS = 24; // Cart expires after 24 hours

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Get cart for a user
     */
    public Cart getCart(Long userId) {
        logger.info("Fetching cart for user: {}", userId);

        String key = getCartKey(userId);
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);

        if (cart == null) {
            logger.info("Creating new cart for user: {}", userId);
            cart = new Cart();
            cart.setUserId(userId);
        }

        return cart;
    }

    /**
     * Add item to cart
     */
    public Cart addToCart(Long userId, AddToCartRequest request) {
        logger.info("Adding product {} to cart for user {}", request.getProductId(), userId);

        Cart cart = getCart(userId);

        // Check if item already exists in cart
        CartItem existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst()
            .orElse(null);

        if (existingItem != null) {
            // Update quantity if item already exists
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            logger.info("Updated quantity for product {} to {}", request.getProductId(), existingItem.getQuantity());
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem(
                request.getProductId(),
                request.getProductName(),
                request.getPrice(),
                request.getQuantity()
            );
            cart.getItems().add(newItem);
            logger.info("Added new product {} to cart", request.getProductId());
        }

        saveCart(cart);
        return cart;
    }

    /**
     * Remove item from cart
     */
    public Cart removeFromCart(Long userId, Long productId) {
        logger.info("Removing product {} from cart for user {}", productId, userId);

        Cart cart = getCart(userId);

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        logger.info("Removed product {} from cart", productId);

        saveCart(cart);
        return cart;
    }

    /**
     * Update item quantity in cart
     */
    public Cart updateQuantity(Long userId, Long productId, Integer quantity) {
        logger.info("Updating quantity for product {} to {} for user {}", productId, quantity, userId);

        Cart cart = getCart(userId);

        CartItem item = cart.getItems().stream()
            .filter(cartItem -> cartItem.getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            logger.info("Removed product {} from cart (quantity set to 0)", productId);
        } else {
            item.setQuantity(quantity);
            logger.info("Updated quantity for product {} to {}", productId, quantity);
        }

        saveCart(cart);
        return cart;
    }

    /**
     * Clear cart for a user
     */
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user: {}", userId);

        String key = getCartKey(userId);
        redisTemplate.delete(key);

        logger.info("Cart cleared for user: {}", userId);
    }

    /**
     * Save cart to Redis with TTL
     */
    private void saveCart(Cart cart) {
        String key = getCartKey(cart.getUserId());
        redisTemplate.opsForValue().set(key, cart, CART_TTL_HOURS, TimeUnit.HOURS);
        logger.debug("Cart saved to Redis for user: {}", cart.getUserId());
    }

    /**
     * Generate Redis key for user cart
     */
    private String getCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }
}
