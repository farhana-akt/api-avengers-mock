package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Inventory Service
 *
 * Business logic for inventory management with reserve/release operations.
 */
@Service
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Get all inventory records
     */
    public List<InventoryResponse> getAllInventory() {
        logger.info("Fetching all inventory records");
        return inventoryRepository.findAll()
            .stream()
            .map(InventoryResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get inventory for a specific product
     */
    public InventoryResponse getInventoryByProductId(Long productId) {
        logger.info("Fetching inventory for product ID: {}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        return InventoryResponse.fromEntity(inventory);
    }

    /**
     * Check if product is in stock
     */
    public boolean isInStock(Long productId, Integer quantity) {
        logger.info("Verifying stock availability for product {} (quantity: {})", productId, quantity);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElse(null);

        if (inventory == null) {
            logger.warn("Product {} not found in inventory", productId);
            return false;
        }

        boolean inStock = inventory.getAvailableQuantity() >= quantity;
        logger.info("Product {} stock check: {} (available: {})", productId, inStock, inventory.getAvailableQuantity());
        return inStock;
    }

    /**
     * Reserve stock for an order (reduce available quantity, increase reserved)
     * Uses pessimistic locking to prevent concurrent updates
     */
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        logger.info("Reserving {} units of product {}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        // Check if enough stock is available
        if (inventory.getAvailableQuantity() < quantity) {
            String message = String.format("Insufficient stock for product %d. Available: %d, Requested: %d",
                productId, inventory.getAvailableQuantity(), quantity);
            logger.error(message);
            throw new RuntimeException(message);
        }

        // Reserve the stock
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        inventoryRepository.save(inventory);
        logger.info("Successfully reserved {} units of product {}", quantity, productId);
    }

    /**
     * Release reserved stock back to available (on order cancellation or payment failure)
     */
    @Transactional
    public void releaseStock(Long productId, Integer quantity) {
        logger.info("Releasing {} units of product {}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        // Check if enough reserved stock exists
        if (inventory.getReservedQuantity() < quantity) {
            String message = String.format("Cannot release stock for product %d. Reserved: %d, Requested: %d",
                productId, inventory.getReservedQuantity(), quantity);
            logger.error(message);
            throw new RuntimeException(message);
        }

        // Release the stock back to available
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        inventoryRepository.save(inventory);
        logger.info("Successfully released {} units of product {}", quantity, productId);
    }

    /**
     * Confirm stock reservation (on successful payment - remove from reserved)
     */
    @Transactional
    public void confirmReservation(Long productId, Integer quantity) {
        logger.info("Confirming reservation of {} units for product {}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        // Check if enough reserved stock exists
        if (inventory.getReservedQuantity() < quantity) {
            String message = String.format("Cannot confirm reservation for product %d. Reserved: %d, Requested: %d",
                productId, inventory.getReservedQuantity(), quantity);
            logger.error(message);
            throw new RuntimeException(message);
        }

        // Remove from reserved (stock is now sold)
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        inventoryRepository.save(inventory);
        logger.info("Successfully confirmed reservation of {} units for product {}", quantity, productId);
    }

    /**
     * Add stock (admin operation)
     */
    @Transactional
    public InventoryResponse addStock(Long productId, Integer quantity) {
        logger.info("Adding {} units to product {}", quantity, productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseGet(() -> {
                Inventory newInventory = new Inventory();
                newInventory.setProductId(productId);
                newInventory.setAvailableQuantity(0);
                newInventory.setReservedQuantity(0);
                return newInventory;
            });

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory = inventoryRepository.save(inventory);

        logger.info("Successfully added {} units to product {}", quantity, productId);
        return InventoryResponse.fromEntity(inventory);
    }
}
