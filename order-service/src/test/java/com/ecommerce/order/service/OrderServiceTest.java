package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.config.RabbitMQConfig;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.event.OrderEvent;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for OrderService using Mockito
 * Includes circuit breaker and saga pattern testing
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    private CartDTO testCart;
    private Order testOrder;
    private PaymentResponse successPaymentResponse;
    private PaymentResponse failedPaymentResponse;
    private final Long userId = 1L;
    private final String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        // Setup test cart
        testCart = new CartDTO();
        CartDTO.CartItemDTO item1 = new CartDTO.CartItemDTO();
        item1.setProductId(101L);
        item1.setProductName("Product 1");
        item1.setPrice(new BigDecimal("100.00"));
        item1.setQuantity(2);
        item1.setSubtotal(new BigDecimal("200.00"));

        CartDTO.CartItemDTO item2 = new CartDTO.CartItemDTO();
        item2.setProductId(102L);
        item2.setProductName("Product 2");
        item2.setPrice(new BigDecimal("50.00"));
        item2.setQuantity(1);
        item2.setSubtotal(new BigDecimal("50.00"));

        testCart.setItems(Arrays.asList(item1, item2));
        testCart.setTotalPrice(new BigDecimal("250.00"));

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(userId);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("250.00"));

        // Setup payment responses
        successPaymentResponse = new PaymentResponse();
        successPaymentResponse.setPaymentId("PAY-123");
        successPaymentResponse.setStatus("SUCCESS");
        successPaymentResponse.setMessage("Payment successful");

        failedPaymentResponse = new PaymentResponse();
        failedPaymentResponse.setStatus("FAILED");
        failedPaymentResponse.setMessage("Insufficient funds");
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(cartClient.getCart(anyString())).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(inventoryClient).reserveStock(any(ReserveStockRequest.class));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(successPaymentResponse);
        doNothing().when(inventoryClient).confirmReservation(any(ReserveStockRequest.class));
        doNothing().when(cartClient).clearCart(anyString());
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(OrderEvent.class));

        // Act
        OrderResponse response = orderService.createOrder(userId, userEmail);

        // Assert
        assertNotNull(response);
        verify(cartClient, times(1)).getCart(userId.toString());
        verify(orderRepository, atLeast(1)).save(any(Order.class));
        verify(inventoryClient, times(2)).reserveStock(any(ReserveStockRequest.class));
        verify(paymentClient, times(1)).processPayment(any(PaymentRequest.class));
        verify(inventoryClient, times(2)).confirmReservation(any(ReserveStockRequest.class));
        verify(cartClient, times(1)).clearCart(userId.toString());
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitMQConfig.ORDER_EXCHANGE),
            eq(RabbitMQConfig.ORDER_ROUTING_KEY),
            any(OrderEvent.class)
        );
    }

    @Test
    void testCreateOrder_EmptyCart() {
        // Arrange
        CartDTO emptyCart = new CartDTO();
        emptyCart.setItems(Collections.emptyList());
        when(cartClient.getCart(anyString())).thenReturn(emptyCart);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(userId, userEmail);
        });

        assertEquals("Cart is empty", exception.getMessage());
        verify(cartClient, times(1)).getCart(userId.toString());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_PaymentFailed() {
        // Arrange
        when(cartClient.getCart(anyString())).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(inventoryClient).reserveStock(any(ReserveStockRequest.class));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(failedPaymentResponse);
        doNothing().when(inventoryClient).releaseStock(any(ReserveStockRequest.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(userId, userEmail);
        });

        assertTrue(exception.getMessage().contains("Payment failed"));
        verify(cartClient, times(1)).getCart(userId.toString());
        verify(inventoryClient, times(2)).reserveStock(any(ReserveStockRequest.class));
        verify(paymentClient, times(1)).processPayment(any(PaymentRequest.class));
        verify(inventoryClient, times(2)).releaseStock(any(ReserveStockRequest.class));
        verify(cartClient, never()).clearCart(anyString());
    }

    @Test
    void testCreateOrder_InventoryReservationFailed() {
        // Arrange
        when(cartClient.getCart(anyString())).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doThrow(new RuntimeException("Insufficient stock")).when(inventoryClient).reserveStock(any(ReserveStockRequest.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(userId, userEmail);
        });

        assertTrue(exception.getMessage().contains("Failed to create order"));
        verify(cartClient, times(1)).getCart(userId.toString());
        verify(inventoryClient, atLeastOnce()).reserveStock(any(ReserveStockRequest.class));
        verify(paymentClient, never()).processPayment(any(PaymentRequest.class));
    }

    @Test
    void testCreateOrder_CircuitBreakerFallback() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFallback(userId, userEmail, new RuntimeException("Service unavailable"));
        });

        assertTrue(exception.getMessage().contains("temporarily unavailable"));
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        OrderResponse response = orderService.getOrderById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(999L);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void testGetUserOrders_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(orders);

        // Act
        List<OrderResponse> response = orderService.getUserOrders(userId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void testGetUserOrders_EmptyList() {
        // Arrange
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());

        // Act
        List<OrderResponse> response = orderService.getUserOrders(userId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        testOrder.setStatus(Order.OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(inventoryClient).releaseStock(any(ReserveStockRequest.class));

        // Act
        OrderResponse response = orderService.cancelOrder(1L, userId);

        // Assert
        assertNotNull(response);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Unauthorized() {
        // Arrange
        testOrder.setUserId(999L); // Different user
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L, userId);
        });

        assertTrue(exception.getMessage().contains("Unauthorized"));
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCancelOrder_InvalidStatus() {
        // Arrange
        testOrder.setStatus(Order.OrderStatus.COMPLETED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(1L, userId);
        });

        assertTrue(exception.getMessage().contains("Cannot cancel order"));
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testSagaPattern_CompensatingTransaction() {
        // This test verifies the saga pattern - if payment fails, inventory should be released
        // Arrange
        when(cartClient.getCart(anyString())).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(inventoryClient).reserveStock(any(ReserveStockRequest.class));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(failedPaymentResponse);
        doNothing().when(inventoryClient).releaseStock(any(ReserveStockRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(userId, userEmail);
        });

        // Verify compensating transaction (inventory release) was called
        verify(inventoryClient, times(2)).releaseStock(any(ReserveStockRequest.class));
    }
}
