package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.ReserveStockRequest;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory Controller
 *
 * REST API endpoints for inventory management.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    /**
     * Get all inventory
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        List<InventoryResponse> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventory);
    }

    /**
     * Get inventory by product ID
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long productId) {
        try {
            InventoryResponse inventory = inventoryService.getInventoryByProductId(productId);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            logger.error("Failed to get inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Check if product is in stock
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkStock(
        @PathVariable Long productId,
        @RequestParam Integer quantity
    ) {
        boolean inStock = inventoryService.isInStock(productId, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("quantity", quantity);
        response.put("inStock", inStock);

        return ResponseEntity.ok(response);
    }

    /**
     * Reserve stock (called by Order Service)
     */
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        try {
            inventoryService.reserveStock(request.getProductId(), request.getQuantity());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock reserved successfully");
            response.put("productId", request.getProductId().toString());
            response.put("quantity", request.getQuantity().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to reserve stock: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Release stock (called by Order Service on cancellation)
     */
    @PostMapping("/release")
    public ResponseEntity<?> releaseStock(@Valid @RequestBody ReserveStockRequest request) {
        try {
            inventoryService.releaseStock(request.getProductId(), request.getQuantity());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock released successfully");
            response.put("productId", request.getProductId().toString());
            response.put("quantity", request.getQuantity().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to release stock: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Confirm stock reservation (called by Order Service after successful payment)
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReservation(@Valid @RequestBody ReserveStockRequest request) {
        try {
            inventoryService.confirmReservation(request.getProductId(), request.getQuantity());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock reservation confirmed");
            response.put("productId", request.getProductId().toString());
            response.put("quantity", request.getQuantity().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to confirm reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Add stock (admin operation)
     */
    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addStock(
        @PathVariable Long productId,
        @RequestParam Integer quantity,
        @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {
        try {
            // Check if user is admin
            if (userRole == null || !userRole.equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorResponse("Only administrators can add stock"));
            }

            InventoryResponse inventory = inventoryService.addStock(productId, quantity);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            logger.error("Failed to add stock: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse(e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "inventory-service");
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create error response
     */
    private Map<String, String> errorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
