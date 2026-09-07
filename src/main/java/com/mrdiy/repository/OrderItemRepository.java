package com.mrdiy.repository;

import com.mrdiy.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderId(UUID orderId);
    Optional<OrderItem> findByIdAndOrderId(UUID id, UUID orderId);
}
