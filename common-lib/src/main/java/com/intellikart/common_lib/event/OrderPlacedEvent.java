package com.intellikart.common_lib.event;

import com.intellikart.common_lib.dto.OrderItemDto;
import java.util.List;

public record OrderPlacedEvent(
        Long orderId,
        String customerId,
        List<OrderItemDto> items
) {}