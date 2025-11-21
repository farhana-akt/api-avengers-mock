package com.ecommerce.cart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Cart Item Model
 *
 * Represents a single item in the shopping cart.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

    /**
     * Get subtotal for this cart item
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
