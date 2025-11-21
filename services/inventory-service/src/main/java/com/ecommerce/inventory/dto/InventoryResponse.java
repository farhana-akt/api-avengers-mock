package com.ecommerce.inventory.dto;

import com.ecommerce.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inventory Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private Long id;
    private Long productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer totalQuantity;
    private Boolean inStock;

    public static InventoryResponse fromEntity(Inventory inventory) {
        return new InventoryResponse(
            inventory.getId(),
            inventory.getProductId(),
            inventory.getAvailableQuantity(),
            inventory.getReservedQuantity(),
            inventory.getTotalQuantity(),
            inventory.getAvailableQuantity() > 0
        );
    }
}
