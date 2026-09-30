package com.intellikart.common_lib.event;

import com.intellikart.common_lib.dto.OrderItemDto;
import java.util.List;

public record PaymentFailedEvent(
        Long orderId,
        String customerId,
        String reason,
        List<OrderItemDto> items
) {}
