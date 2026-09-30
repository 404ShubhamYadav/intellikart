package com.intellikart.common_lib.command;
import com.intellikart.common_lib.dto.OrderItemDto;
import java.util.List;
public record ProcessPaymentCommand(
        Long orderId,
        String customerId,
        List<OrderItemDto> items
) {}
