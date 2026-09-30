package com.intellikart.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(
        @NotBlank(message = "productId is required")
        String productId,

        @NotBlank(message = "name is required")
        String name,

        String description,

        @NotNull @PositiveOrZero(message = "availableQuantity must be zero or positive")
        Integer availableQuantity
) {}
