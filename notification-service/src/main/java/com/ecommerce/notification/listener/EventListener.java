package com.ecommerce.notification.listener;

import com.ecommerce.notification.config.RabbitMQConfig;
import com.ecommerce.notification.event.OrderEvent;
import com.ecommerce.notification.event.PaymentEvent;
import com.ecommerce.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Event Listener
 *
 * Listens to RabbitMQ queues for order and payment events.
 */
@Component
public class EventListener {

    private static final Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * Listen to order events
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderEvent(OrderEvent event) {
        logger.info("Received order event: Order ID {}, Status: {}", event.getOrderId(), event.getStatus());

        try {
            switch (event.getStatus()) {
                case "COMPLETED":
                    notificationService.sendOrderConfirmation(
                        event.getUserEmail(),
                        event.getOrderId(),
                        event.getTotalAmount().toString()
                    );
                    break;
                case "CANCELLED":
                    notificationService.sendOrderCancellation(
                        event.getUserEmail(),
                        event.getOrderId()
                    );
                    break;
                default:
                    logger.info("Order event received but no notification needed for status: {}", event.getStatus());
            }
        } catch (Exception e) {
            logger.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    /**
     * Listen to payment events
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void handlePaymentEvent(PaymentEvent event) {
        logger.info("Received payment event: Payment ID {}, Status: {}", event.getPaymentId(), event.getStatus());

        try {
            // In a real system, you might fetch user email from User Service
            // For now, we'll just log the event
            logger.info("Payment processed: {} for order {} with status: {}",
                event.getPaymentId(), event.getOrderId(), event.getStatus());

            // You could send payment confirmation emails here if needed
            // notificationService.sendPaymentConfirmation(...);

        } catch (Exception e) {
            logger.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }
}
