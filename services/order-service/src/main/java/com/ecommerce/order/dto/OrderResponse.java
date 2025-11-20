package com.ecommerce.order.dto;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
    private String paymentId;
    private LocalDateTime createdAt;

    public static OrderResponse fromEntity(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
            .map(OrderItemDTO::fromEntity)
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getUserId(),
            order.getStatus().name(),
            order.getTotalAmount(),
            itemDTOs,
            order.getPaymentId(),
            order.getCreatedAt()
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;

        public static OrderItemDTO fromEntity(OrderItem item) {
            return new OrderItemDTO(
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
            );
        }
    }
}
