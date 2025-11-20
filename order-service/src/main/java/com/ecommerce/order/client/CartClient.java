package com.ecommerce.order.client;

import com.ecommerce.order.dto.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign Client for Cart Service
 */
@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/api/cart")
    CartDTO getCart(@RequestHeader("X-User-Id") String userId);

    @DeleteMapping("/api/cart/clear")
    void clearCart(@RequestHeader("X-User-Id") String userId);
}
