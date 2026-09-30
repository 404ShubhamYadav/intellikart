package com.intellikart.orchestration_service.enums;

public enum SagaStatus {
    STARTED,
    STOCK_RESERVED,
    PAYMENT_COMPLETED,
    ORDER_CONFIRMED,
    STOCK_RESERVATION_FAILED,
    PAYMENT_FAILED,
    COMPENSATING,
    FAILED
}
