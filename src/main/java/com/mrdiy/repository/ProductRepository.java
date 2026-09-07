package com.mrdiy.repository;

import com.mrdiy.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySlug(String slug);

    @Query("select p from Product p where " +
            "(:categoryId is null or p.category.id = :categoryId) and " +
            "(:search is null or lower(p.name) like lower(concat('%', :search, '%')) " +
            "or lower(p.sku) like lower(concat('%', :search, '%')) " +
            "or lower(p.shortDescription) like lower(concat('%', :search, '%')))")
    Page<Product> search(@Param("categoryId") UUID categoryId, @Param("search") String search, Pageable pageable);
}
