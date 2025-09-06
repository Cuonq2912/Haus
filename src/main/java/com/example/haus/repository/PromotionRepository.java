package com.example.haus.repository;

import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    boolean existsByPromotionName(String promotionName);

    Optional<Promotion> findByPromotionNameIgnoreCase(String promotionName);
}
