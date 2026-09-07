package com.mrdiy.repository;

import com.mrdiy.domain.ReturnedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReturnedItemRepository extends JpaRepository<ReturnedItem, UUID> {
    Optional<ReturnedItem> findByOrderItemId(UUID orderItemId);
}
