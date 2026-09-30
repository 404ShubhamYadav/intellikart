package com.intellikart.order_service.converter;

import com.intellikart.common_lib.dto.OrderItemDto;
import com.intellikart.common_lib.event.OrderPlacedEvent;
import com.intellikart.order_service.dto.CreateOrderRequest;
import com.intellikart.order_service.dto.OrderItemResponse;
import com.intellikart.order_service.dto.OrderResponse;
import com.intellikart.order_service.entity.Order;
import com.intellikart.order_service.entity.OrderLineItem;
import com.intellikart.order_service.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderConverter {

    public Order toEntity(String customerId, CreateOrderRequest request) {
        Order order = Order.builder()
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderLineItem> lineItems = request.items().stream()
                .map(item -> toLineItemEntity(item, order))
                .toList();

        order.setItems(lineItems);
        return order;
    }

    private OrderLineItem toLineItemEntity(OrderItemDto dto, Order order) {
        return OrderLineItem.builder()
                .productId(dto.productId())
                .quantity(dto.quantity())
                .order(order)
                .build();
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(item.getProductId(), item.getQuantity()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                items,
                order.getCreatedAt()
        );
    }
    public OrderPlacedEvent toEvent(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderItemDto(item.getProductId(), item.getQuantity()))
                .toList();

        return new OrderPlacedEvent(order.getId(), order.getCustomerId(), items);
    }
}
