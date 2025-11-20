package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.config.RabbitMQConfig;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.event.OrderEvent;
import com.ecommerce.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service
 *
 * Orchestrates the order process with circuit breakers and compensating transactions.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Create order from user's cart
     * Uses Circuit Breaker for payment service calls
     */
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrder(Long userId, String userEmail) {
        logger.info("Creating order for user: {}", userId);

        // Step 1: Get cart items
        CartDTO cart = cartClient.getCart(userId.toString());

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Step 2: Create order entity
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(cart.getTotalPrice());

        // Add items to order
        for (CartDTO.CartItemDTO cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            order.addItem(orderItem);
        }

        order = orderRepository.save(order);
        logger.info("Order created with ID: {}", order.getId());

        try {
            // Step 3: Reserve inventory for all items
            logger.info("Reserving inventory for order: {}", order.getId());
            for (OrderItem item : order.getItems()) {
                ReserveStockRequest reserveRequest = new ReserveStockRequest(
                    item.getProductId(),
                    item.getQuantity()
                );
                inventoryClient.reserveStock(reserveRequest);
            }

            // Step 4: Process payment
            order.setStatus(Order.OrderStatus.PAYMENT_PROCESSING);
            orderRepository.save(order);

            logger.info("Processing payment for order: {}", order.getId());
            PaymentRequest paymentRequest = new PaymentRequest(
                order.getId(),
                userId,
                order.getTotalAmount()
            );

            PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

            if ("SUCCESS".equals(paymentResponse.getStatus())) {
                // Payment successful
                order.setStatus(Order.OrderStatus.COMPLETED);
                order.setPaymentId(paymentResponse.getPaymentId());
                orderRepository.save(order);

                // Confirm inventory reservation (remove from reserved)
                for (OrderItem item : order.getItems()) {
                    ReserveStockRequest confirmRequest = new ReserveStockRequest(
                        item.getProductId(),
                        item.getQuantity()
                    );
                    inventoryClient.confirmReservation(confirmRequest);
                }

                // Clear user's cart
                cartClient.clearCart(userId.toString());

                // Publish order placed event
                publishOrderEvent(order, userEmail);

                logger.info("Order completed successfully: {}", order.getId());
            } else {
                // Payment failed - rollback inventory
                logger.error("Payment failed for order: {}", order.getId());
                order.setStatus(Order.OrderStatus.PAYMENT_FAILED);
                orderRepository.save(order);

                // Release reserved inventory
                releaseInventory(order);

                throw new RuntimeException("Payment failed: " + paymentResponse.getMessage());
            }

        } catch (Exception e) {
            // If any step fails, rollback inventory and update order status
            logger.error("Error creating order: {}", e.getMessage());
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);

            // Attempt to release inventory
            try {
                releaseInventory(order);
            } catch (Exception ex) {
                logger.error("Failed to release inventory: {}", ex.getMessage());
            }

            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }

        return OrderResponse.fromEntity(order);
    }

    /**
     * Fallback method for circuit breaker
     */
    public OrderResponse createOrderFallback(Long userId, String userEmail, Exception e) {
        logger.error("Circuit breaker activated for user {}: {}", userId, e.getMessage());
        throw new RuntimeException("Order service is temporarily unavailable. Please try again later.");
    }

    /**
     * Release reserved inventory (compensating transaction)
     */
    private void releaseInventory(Order order) {
        logger.info("Releasing inventory for order: {}", order.getId());
        for (OrderItem item : order.getItems()) {
            try {
                ReserveStockRequest releaseRequest = new ReserveStockRequest(
                    item.getProductId(),
                    item.getQuantity()
                );
                inventoryClient.releaseStock(releaseRequest);
            } catch (Exception e) {
                logger.error("Failed to release stock for product {}: {}", item.getProductId(), e.getMessage());
            }
        }
    }

    /**
     * Publish order event to RabbitMQ
     */
    private void publishOrderEvent(Order order, String userEmail) {
        OrderEvent event = new OrderEvent(
            order.getId(),
            order.getUserId(),
            userEmail,
            order.getTotalAmount(),
            order.getStatus().name(),
            LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ORDER_ROUTING_KEY,
            event
        );

        logger.info("Published order event for order: {}", order.getId());
    }

    /**
     * Get order by ID
     */
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        return OrderResponse.fromEntity(order);
    }

    /**
     * Get all orders for a user
     */
    public List<OrderResponse> getUserOrders(Long userId) {
        logger.info("Fetching orders for user: {}", userId);

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Cancel order
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        logger.info("Cancelling order: {}", orderId);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify order belongs to user
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        // Only pending or payment failed orders can be cancelled
        if (order.getStatus() != Order.OrderStatus.PENDING &&
            order.getStatus() != Order.OrderStatus.PAYMENT_FAILED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Release inventory if it was reserved
        releaseInventory(order);

        logger.info("Order cancelled: {}", orderId);
        return OrderResponse.fromEntity(order);
    }
}
