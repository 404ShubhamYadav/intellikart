package com.intellikart.order_service.dto;

import com.intellikart.order_service.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerId,
        OrderStatus status,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {}
