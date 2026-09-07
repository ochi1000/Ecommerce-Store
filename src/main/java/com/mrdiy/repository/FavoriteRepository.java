package com.mrdiy.repository;

import com.mrdiy.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
    List<Favorite> findByUserId(UUID userId);
    Optional<Favorite> findByUserIdAndProductId(UUID userId, UUID productId);
}
