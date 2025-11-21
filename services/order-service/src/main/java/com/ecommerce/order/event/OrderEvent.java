package com.ecommerce.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Event
 *
 * Event published to RabbitMQ when order is placed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long userId;
    private String userEmail;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime timestamp;
}
