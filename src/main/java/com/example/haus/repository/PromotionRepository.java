package com.example.haus.repository;

import com.example.haus.domain.entity.product.Promotion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByPromotionCodeAndIsDeletedFalse(String promotionCode);

    Boolean existsByPromotionCodeAndIsDeletedFalse(String promotionCode);

    Boolean existsByPromotionCodeAndIsDeletedTrue(String promotionCode);

    Optional<Promotion> findByIdAndIsDeletedFalse(Long id);

    //Update scheduled
    @Modifying
    @Transactional
    @Query("UPDATE Promotion p SET p.status = 'EXPIRED' WHERE p.endDate < :currentTime AND p.status = 'ACTIVE'")
    int updateExpiredPromotions(@Param("currentTime") LocalDate currentTime);

}
