package com.mrdiy.repository;

import com.mrdiy.domain.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {
    List<DeliveryAddress> findByUserId(UUID userId);
    Optional<DeliveryAddress> findByIdAndUserId(UUID id, UUID userId);
}
