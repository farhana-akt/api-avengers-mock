package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Order Controller
 *
 * REST API endpoints for order management.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Create order from cart
     * Requires authentication - user ID and email come from JWT headers
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
        @RequestHeader("X-User-Id") String userId,
        @RequestHeader("X-User-Email") String userEmail
    ) {
        try {
            OrderResponse order = orderService.createOrder(Long.parseLong(userId), userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            logger.error("Failed to create order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(
        @PathVariable Long orderId,
        @RequestHeader("X-User-Id") String userId
    ) {
        try {
            OrderResponse order = orderService.getOrderById(orderId);

            // Verify order belongs to user
            if (!order.getUserId().equals(Long.parseLong(userId))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorResponse("Unauthorized to view this order"));
            }

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Failed to get order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Get all orders for authenticated user
     */
    @GetMapping
    public ResponseEntity<?> getUserOrders(@RequestHeader("X-User-Id") String userId) {
        try {
            List<OrderResponse> orders = orderService.getUserOrders(Long.parseLong(userId));
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Failed to get user orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Cancel order
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
        @PathVariable Long orderId,
        @RequestHeader("X-User-Id") String userId
    ) {
        try {
            OrderResponse order = orderService.cancelOrder(orderId, Long.parseLong(userId));
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Failed to cancel order: {}", e.getMessage());
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
        response.put("service", "order-service");
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
