package com.mrdiy.repository;

import com.mrdiy.domain.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductTagRepository extends JpaRepository<ProductTag, UUID> {
}
