package com.example.haus.repository;

import com.example.haus.domain.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByProductNameAndIsDeletedFalse(String name);

    Boolean existsByProductNameAndIsDeletedTrue(String name);

    Boolean existsByProductCode(String productCode);

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.categories c " +
            "WHERE c.categoryName = :categoryName " +
            "AND (p.isDeleted IS NULL OR p.isDeleted = false)")
    Page<Product> findProductsByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.categories c " +
            "WHERE c.id = :categoryId " +
            "AND (p.isDeleted IS NULL OR p.isDeleted = false)")
    Page<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (LOWER(CAST(p.description AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(CAST(p.detailDescription AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (p.isDeleted IS NULL OR p.isDeleted = false)")
    Page<Product> searchProductsByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
