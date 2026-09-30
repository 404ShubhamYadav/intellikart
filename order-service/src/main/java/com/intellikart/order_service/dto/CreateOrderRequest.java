package com.intellikart.order_service.dto;

import com.intellikart.common_lib.dto.OrderItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemDto> items
) {}