package com.intellikart.common_lib.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemDto(
        @NotNull String productId,
        @NotNull @Positive Integer quantity
) {}
