package com.mrdiy.repository;

import com.mrdiy.domain.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, UUID> {
    List<ProductAttribute> findByProductId(UUID productId);
    Optional<ProductAttribute> findByProductIdAndAttributeId(UUID productId, UUID attributeId);
}
