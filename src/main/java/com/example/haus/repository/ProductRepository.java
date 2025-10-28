package com.example.haus.repository;

import com.example.haus.domain.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.categories c " +
            "LEFT JOIN FETCH p.medias m " +
            "LEFT JOIN FETCH p.productVariations pv " +
            "WHERE p.id = :id AND p.isDeleted = false " +
            "AND (pv IS NULL OR pv.isDeleted = false)")
    Product findByIdWithActiveVariations(@Param("id") Long id);

    Boolean existsByProductNameAndIsDeletedFalse(String name);

    Boolean existsByProductCode(String productCode);

    @Query("SELECT p FROM Product p WHERE (p.isDeleted IS NULL OR p.isDeleted = false)")
    Page<Product> findAllActiveProducts(Pageable pageable);


}
