package com.intellikart.common_lib.event;

public record PaymentCompletedEvent(
        Long orderId,
        String customerId
) {}
