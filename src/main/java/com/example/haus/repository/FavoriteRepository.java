package com.example.haus.repository;

import com.example.haus.domain.dto.response.product.FavoriteResponseDto;
import com.example.haus.domain.entity.product.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Kiểm tra sản phẩm đã được user yêu thích chưa
    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
            FROM Favorite f
            WHERE f.user.id = :userId AND f.product.id = :productId
    """)
    boolean existsByUserIdAndProductId(
            @Param("userId") String userId,
            @Param("productId") Long productId
    );


    @Query("""
            SELECT f
            FROM Favorite f
            WHERE f.user.id = :userId AND f.product.id = :productId
    """)
    Optional<Favorite> findByUserIdAndProductId(
            @Param("userId") String userId,
            @Param("productId") Long productId
    );

    @Query("""
        SELECT new com.example.haus.domain.dto.response.product.FavoriteResponseDto(
                               f.id,
                               f.createdAt,
                               p.id,
                               p.productCode,
                               p.productName,
                               p.price,
                               (SELECT MIN(m.url) FROM medias m WHERE m.product.id = p.id)
                           )
                               FROM Favorite f
                                   JOIN f.product p
                                       WHERE f.user.id = :userId
                                           AND p.isDeleted = false
                                               ORDER BY f.createdAt DESC
    """)
    Page<FavoriteResponseDto> findByUserIdWithProductDetails(
            @Param("userId") String userId,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(f)
        FROM Favorite f
        WHERE f.user.id = :userId
    """)
    long countByUserId(@Param("userId") String userId);

    @Modifying
    @Query("""
        DELETE FROM Favorite f
        WHERE f.user.id = :userId
          AND f.product.id = :productId
    """)
    int deleteByUserIdAndProductId(
        @Param("userId") String userId,
        @Param("productId") Long productId
    );
}
