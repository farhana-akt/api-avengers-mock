package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reserve Stock Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {
    private Long productId;
    private Integer quantity;
}
