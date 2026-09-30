package com.intellikart.inventory_service.dto;

public record ProductResponse(
        String productId,
        String name,
        String description,
        Integer availableQuantity
) {}
