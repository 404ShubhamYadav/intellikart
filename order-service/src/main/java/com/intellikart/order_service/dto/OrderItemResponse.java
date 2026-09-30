package com.intellikart.order_service.dto;

public record OrderItemResponse(
        String productId,
        Integer quantity
) {}
