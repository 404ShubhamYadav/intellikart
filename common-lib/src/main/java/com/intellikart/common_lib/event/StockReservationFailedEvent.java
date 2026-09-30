package com.intellikart.common_lib.event;

public record StockReservationFailedEvent(
        Long orderId,
        String customerId,
        String reason
) {}
