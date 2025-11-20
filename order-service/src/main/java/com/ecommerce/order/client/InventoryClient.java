package com.ecommerce.order.client;

import com.ecommerce.order.dto.ReserveStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for Inventory Service
 */
@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    void reserveStock(@RequestBody ReserveStockRequest request);

    @PostMapping("/api/inventory/release")
    void releaseStock(@RequestBody ReserveStockRequest request);

    @PostMapping("/api/inventory/confirm")
    void confirmReservation(@RequestBody ReserveStockRequest request);
}
