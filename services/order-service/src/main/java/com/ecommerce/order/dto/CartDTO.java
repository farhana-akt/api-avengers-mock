package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart DTO (from Cart Service)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long userId;
    private List<CartItemDTO> items = new ArrayList<>();
    private BigDecimal totalPrice;
    private Integer totalItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
