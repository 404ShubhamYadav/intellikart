package com.intellikart.common_lib.constant;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String ORDER_PLACED_TOPIC = "order-placed-topic";
    public static final String STOCK_RESERVED_TOPIC = "stock-reserved-topic";
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed-topic";
    public static final String PAYMENT_FAILED_TOPIC = "payment-failed-topic";

    public static final String RESERVE_STOCK_COMMAND_TOPIC = "reserve-stock-command-topic";
    public static final String PROCESS_PAYMENT_COMMAND_TOPIC = "process-payment-command-topic";
    public static final String CONFIRM_ORDER_COMMAND_TOPIC = "confirm-order-command-topic";
    public static final String CANCEL_ORDER_COMMAND_TOPIC = "cancel-order-command-topic";
    public static final String STOCK_RESERVATION_FAILED_TOPIC = "stock-reservation-failed-topic";
    public static final String RELEASE_STOCK_COMMAND_TOPIC = "release-stock-command-topic";
}
