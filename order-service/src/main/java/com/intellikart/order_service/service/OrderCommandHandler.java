package com.intellikart.order_service.service;

import com.intellikart.common_lib.command.CancelOrderCommand;
import com.intellikart.common_lib.command.ConfirmOrderCommand;
import com.intellikart.common_lib.constant.KafkaTopics;
import com.intellikart.order_service.entity.Order;
import com.intellikart.order_service.enums.OrderStatus;
import com.intellikart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCommandHandler {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = KafkaTopics.CONFIRM_ORDER_COMMAND_TOPIC, groupId = "order-service-group")
    public void handleConfirmOrder(ConfirmOrderCommand command) {
        log.info("Received ConfirmOrderCommand for orderId={}", command.orderId());

        orderRepository.findById(command.orderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            log.info("Order {} marked CONFIRMED", command.orderId());
        });
    }

    @KafkaListener(topics = KafkaTopics.CANCEL_ORDER_COMMAND_TOPIC, groupId = "order-service-group")
    public void handleCancelOrder(CancelOrderCommand command) {
        log.info("Received CancelOrderCommand for orderId={}, reason={}", command.orderId(), command.reason());

        orderRepository.findById(command.orderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Order {} marked CANCELLED", command.orderId());
        });
    }
}
