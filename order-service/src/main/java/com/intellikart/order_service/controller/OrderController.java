package com.intellikart.order_service.controller;

import com.intellikart.order_service.dto.CreateOrderRequest;
import com.intellikart.order_service.dto.OrderResponse;
import com.intellikart.order_service.entity.Order;
import com.intellikart.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(customerId, request));
    }
}
