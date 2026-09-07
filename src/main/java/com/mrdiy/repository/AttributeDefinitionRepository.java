package com.mrdiy.repository;

import com.mrdiy.domain.AttributeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID> {
}
