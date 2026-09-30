package com.intellikart.orchestration_service.repository;

import com.intellikart.orchestration_service.entity.OrderSaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderSagaRepository extends JpaRepository<OrderSaga, Long> {
}
