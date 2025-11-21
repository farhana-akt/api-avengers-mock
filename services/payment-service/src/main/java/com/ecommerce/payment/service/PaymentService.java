package com.ecommerce.payment.service;

import com.ecommerce.payment.config.RabbitMQConfig;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.event.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * Payment Service
 *
 * Mock payment processing with 90% success rate.
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private static final Random random = new Random();
    private static final double SUCCESS_RATE = 0.9; // 90% success rate

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Process payment with mock implementation
     * 90% chance of success, 10% chance of failure
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment for order: {} (amount: {})", request.getOrderId(), request.getAmount());

        // Generate payment ID
        String paymentId = "PAY-" + UUID.randomUUID().toString();

        // Simulate payment processing delay
        try {
            Thread.sleep(500); // 500ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mock payment processing with 90% success rate
        boolean isSuccess = random.nextDouble() < SUCCESS_RATE;

        PaymentResponse response;
        if (isSuccess) {
            response = new PaymentResponse(
                paymentId,
                "SUCCESS",
                "Payment processed successfully"
            );
            logger.info("Payment successful: {} for order: {}", paymentId, request.getOrderId());
        } else {
            response = new PaymentResponse(
                paymentId,
                "FAILED",
                "Payment failed - Insufficient funds"
            );
            logger.warn("Payment failed: {} for order: {}", paymentId, request.getOrderId());
        }

        // Publish payment event to RabbitMQ
        publishPaymentEvent(paymentId, request, response.getStatus());

        return response;
    }

    /**
     * Publish payment event to RabbitMQ
     */
    private void publishPaymentEvent(String paymentId, PaymentRequest request, String status) {
        PaymentEvent event = new PaymentEvent(
            paymentId,
            request.getOrderId(),
            request.getUserId(),
            request.getAmount(),
            status,
            LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PAYMENT_EXCHANGE,
            RabbitMQConfig.PAYMENT_ROUTING_KEY,
            event
        );

        logger.info("Published payment event: {} for order: {}", paymentId, request.getOrderId());
    }
}
