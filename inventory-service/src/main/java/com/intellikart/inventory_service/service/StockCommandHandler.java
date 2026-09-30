package com.intellikart.inventory_service.service;

import com.intellikart.common_lib.command.ReleaseStockCommand;
import com.intellikart.common_lib.command.ReserveStockCommand;
import com.intellikart.common_lib.constant.KafkaTopics;
import com.intellikart.common_lib.dto.OrderItemDto;
import com.intellikart.common_lib.event.StockReservationFailedEvent;
import com.intellikart.common_lib.event.StockReservedEvent;
import com.intellikart.inventory_service.entity.Product;
import com.intellikart.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockCommandHandler {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.RESERVE_STOCK_COMMAND_TOPIC, groupId = "inventory-service-group")
    public void handleReserveStock(ReserveStockCommand command) {
        log.info("Received ReserveStockCommand for orderId={}", command.orderId());

        for (OrderItemDto item : command.items()) {
            Product product = productRepository.findById(item.productId()).orElse(null);

            if (product == null || product.getAvailableQuantity() < item.quantity()) {
                String reason = product == null
                        ? "Product not found: " + item.productId()
                        : "Insufficient stock for productId=" + item.productId();

                log.warn("Stock reservation failed for orderId={}: {}", command.orderId(), reason);
                kafkaTemplate.send(KafkaTopics.STOCK_RESERVATION_FAILED_TOPIC, command.orderId().toString(),
                        new StockReservationFailedEvent(command.orderId(), command.customerId(), reason));
                return;
            }
        }

        // second pass: only actually decrement once we've confirmed every item is available
        for (OrderItemDto item : command.items()) {
            Product product = productRepository.findById(item.productId()).orElseThrow();
            product.setAvailableQuantity(product.getAvailableQuantity() - item.quantity());
            productRepository.save(product);
            log.info("Reserved {} units of productId={}", item.quantity(), item.productId());
        }

        kafkaTemplate.send(KafkaTopics.STOCK_RESERVED_TOPIC, command.orderId().toString(),
                new StockReservedEvent(command.orderId(), command.customerId(), command.items()));
        log.info("Published StockReservedEvent for orderId={}", command.orderId());
    }

    @KafkaListener(topics = KafkaTopics.RELEASE_STOCK_COMMAND_TOPIC, groupId = "inventory-service-group")
    public void handleReleaseStock(ReleaseStockCommand command) {
        log.info("Received ReleaseStockCommand for orderId={}", command.orderId());

        for (OrderItemDto item : command.items()) {
            productRepository.findById(item.productId()).ifPresent(product -> {
                product.setAvailableQuantity(product.getAvailableQuantity() + item.quantity());
                productRepository.save(product);
                log.info("Released {} units back to productId={}", item.quantity(), item.productId());
            });
        }
    }
}
