package com.intellikart.payment_service.service;

import com.intellikart.common_lib.command.ProcessPaymentCommand;
import com.intellikart.common_lib.constant.KafkaTopics;
import com.intellikart.common_lib.event.PaymentCompletedEvent;
import com.intellikart.common_lib.event.PaymentFailedEvent;
import com.intellikart.payment_service.entity.Payment;
import com.intellikart.payment_service.enums.PaymentStatus;
import com.intellikart.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCommandHandler {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.PROCESS_PAYMENT_COMMAND_TOPIC, groupId = "payment-service-group")
    public void handleProcessPayment(ProcessPaymentCommand command) {
        log.info("Received ProcessPaymentCommand for orderId={}", command.orderId());

        boolean paymentSuccess = mockChargeCustomer(command);

        Payment payment = Payment.builder()
                .orderId(command.orderId())
                .customerId(command.customerId())
                .status(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .processedAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        if (paymentSuccess) {
            kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED_TOPIC, command.orderId().toString(),
                    new PaymentCompletedEvent(command.orderId(), command.customerId()));
            log.info("Payment succeeded, published PaymentCompletedEvent for orderId={}", command.orderId());
        } else {
            kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED_TOPIC, command.orderId().toString(),
                    new PaymentFailedEvent(command.orderId(), command.customerId(), "Mock payment declined", command.items()));
            log.warn("Payment failed, published PaymentFailedEvent for orderId={}", command.orderId());
        }
    }

    private boolean mockChargeCustomer(ProcessPaymentCommand command) {
        // Deliberate test hook: ordering this reserved productId always fails payment,
        // so the compensation path stays demoable/testable without touching real payment logic.
        boolean containsFailTrigger = command.items().stream()
                .anyMatch(item -> "FAIL-TEST".equals(item.productId()));

        return !containsFailTrigger;
    }
}
