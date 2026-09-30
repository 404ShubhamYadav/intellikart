package com.intellikart.orchestration_service.service;

import com.intellikart.common_lib.command.*;
import com.intellikart.common_lib.constant.KafkaTopics;
import com.intellikart.common_lib.event.*;
import com.intellikart.orchestration_service.entity.OrderSaga;
import com.intellikart.orchestration_service.enums.SagaStatus;
import com.intellikart.orchestration_service.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {

    private final OrderSagaRepository sagaRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.ORDER_PLACED_TOPIC, groupId = "orchestration-service-group")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Saga started for orderId={}", event.orderId());

        OrderSaga saga = OrderSaga.builder()
                .orderId(event.orderId())
                .customerId(event.customerId())
                .status(SagaStatus.STARTED)
                .updatedAt(LocalDateTime.now())
                .build();
        sagaRepository.save(saga);

        kafkaTemplate.send(KafkaTopics.RESERVE_STOCK_COMMAND_TOPIC, event.orderId().toString(),
                new ReserveStockCommand(event.orderId(), event.customerId(), event.items()));
        log.info("Sent ReserveStockCommand for orderId={}", event.orderId());
    }

    @KafkaListener(topics = KafkaTopics.STOCK_RESERVED_TOPIC, groupId = "orchestration-service-group")
    public void handleStockReserved(StockReservedEvent event) {
        log.info("Stock reserved for orderId={}, proceeding to payment", event.orderId());
        updateSagaStatus(event.orderId(), SagaStatus.STOCK_RESERVED);

        kafkaTemplate.send(KafkaTopics.PROCESS_PAYMENT_COMMAND_TOPIC, event.orderId().toString(),
                new ProcessPaymentCommand(event.orderId(), event.customerId(), event.items()));
        log.info("Sent ProcessPaymentCommand for orderId={}", event.orderId());
    }

    @KafkaListener(topics = KafkaTopics.STOCK_RESERVATION_FAILED_TOPIC, groupId = "orchestration-service-group")
    public void handleStockReservationFailed(StockReservationFailedEvent event) {
        log.warn("Stock reservation failed for orderId={}: {}", event.orderId(), event.reason());

        updateSagaStatus(event.orderId(), SagaStatus.STOCK_RESERVATION_FAILED);

        kafkaTemplate.send(KafkaTopics.CANCEL_ORDER_COMMAND_TOPIC, event.orderId().toString(),
                new CancelOrderCommand(event.orderId(), event.reason()));
        log.info("Sent CancelOrderCommand for orderId={}", event.orderId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED_TOPIC, groupId = "orchestration-service-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Payment completed for orderId={}, confirming order", event.orderId());

        updateSagaStatus(event.orderId(), SagaStatus.PAYMENT_COMPLETED);

        kafkaTemplate.send(KafkaTopics.CONFIRM_ORDER_COMMAND_TOPIC, event.orderId().toString(),
                new ConfirmOrderCommand(event.orderId()));

        updateSagaStatus(event.orderId(), SagaStatus.ORDER_CONFIRMED);
        log.info("Sent ConfirmOrderCommand for orderId={}", event.orderId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED_TOPIC, groupId = "orchestration-service-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Payment failed for orderId={}: {}. Compensating.", event.orderId(), event.reason());
        updateSagaStatus(event.orderId(), SagaStatus.COMPENSATING);

        kafkaTemplate.send(KafkaTopics.RELEASE_STOCK_COMMAND_TOPIC, event.orderId().toString(),
                new ReleaseStockCommand(event.orderId(), event.items()));
        log.info("Sent ReleaseStockCommand for orderId={}", event.orderId());

        kafkaTemplate.send(KafkaTopics.CANCEL_ORDER_COMMAND_TOPIC, event.orderId().toString(),
                new CancelOrderCommand(event.orderId(), event.reason()));

        updateSagaStatus(event.orderId(), SagaStatus.FAILED);
    }

    private void updateSagaStatus(Long orderId, SagaStatus status) {
        sagaRepository.findById(orderId).ifPresent(saga -> {
            saga.setStatus(status);
            saga.setUpdatedAt(LocalDateTime.now());
            sagaRepository.save(saga);
        });
    }
}
