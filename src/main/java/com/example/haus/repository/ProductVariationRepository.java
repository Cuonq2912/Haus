package com.example.haus.repository;

import com.example.haus.domain.entity.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {

    @Query("SELECT pv FROM ProductVariation pv WHERE pv.product.id = :productId AND pv.isDeleted = FALSE")
    List<ProductVariation> findByProductIdAndIsDeletedFalse(@Param("productId") Long productId);

}
