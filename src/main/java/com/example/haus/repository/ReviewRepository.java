package com.example.haus.repository;

import com.example.haus.domain.entity.product.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.deletedAt IS NULL")
    Page<Review> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.deletedAt IS NULL")
    Page<Review> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.id = :reviewId AND r.deletedAt IS NULL")
    Optional<Review> findByIdAndIsDeletedFalse(@Param("reviewId") Long reviewId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId AND r.deletedAt IS NULL")
    boolean existsByUserIdAndProductId(@Param("userId") String userId, @Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating = :rating AND r.deletedAt IS NULL")
    Page<Review> findByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Integer rating, Pageable pageable);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.deletedAt IS NULL " +
            "AND r.isHidden = false " +
            "ORDER BY r.rating DESC, r.createdAt DESC")
    Page<Review> findTopReviewsByRating(Pageable pageable);
}
