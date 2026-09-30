package com.intellikart.orchestration_service.entity;

import com.intellikart.orchestration_service.enums.SagaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_sagas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSaga {

    @Id
    private Long orderId;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    private LocalDateTime updatedAt;
}
