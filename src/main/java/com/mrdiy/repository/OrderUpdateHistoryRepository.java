package com.mrdiy.repository;

import com.mrdiy.domain.OrderUpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderUpdateHistoryRepository extends JpaRepository<OrderUpdateHistory, UUID> {
}
