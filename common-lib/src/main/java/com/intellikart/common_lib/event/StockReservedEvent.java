package com.intellikart.common_lib.event;

import java.util.List;
import com.intellikart.common_lib.dto.OrderItemDto;

public record StockReservedEvent(
        Long orderId,
        String customerId,
        List<OrderItemDto> items
) {}
