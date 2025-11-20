package com.ecommerce.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Order Request DTO
 */
@Data
@NoArgsConstructor
public class CreateOrderRequest {
    // Order will be created from user's cart
    // No additional fields needed - user ID comes from JWT header
}
