package com.intellikart.order_service.service;
import com.intellikart.common_lib.constant.KafkaTopics;
import com.intellikart.common_lib.event.OrderPlacedEvent;
import com.intellikart.order_service.converter.OrderConverter;
import com.intellikart.order_service.dto.CreateOrderRequest;
import com.intellikart.order_service.dto.OrderResponse;
import com.intellikart.order_service.entity.Order;
import com.intellikart.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderResponse placeOrder(String customerId, CreateOrderRequest request) {
        Order order = orderConverter.toEntity(customerId, request);
        Order saved = orderRepository.save(order);

        OrderPlacedEvent event = orderConverter.toEvent(saved);
        kafkaTemplate.send(KafkaTopics.ORDER_PLACED_TOPIC, saved.getId().toString(), event);

        return orderConverter.toResponse(saved);
    }
}
