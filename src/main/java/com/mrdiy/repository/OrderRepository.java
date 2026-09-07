package com.mrdiy.repository;

import com.mrdiy.domain.Order;
import com.mrdiy.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdAndPaymentStatusNot(UUID userId, PaymentStatus paymentStatus);
}
