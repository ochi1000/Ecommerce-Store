package com.mrdiy.repository;

import com.mrdiy.domain.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, UUID> {
    List<AttributeValue> findByProductAttributeId(UUID productAttributeId);
    boolean existsByProductAttributeIdAndValue(UUID productAttributeId, String value);
}
