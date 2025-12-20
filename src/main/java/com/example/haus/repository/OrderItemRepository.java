package com.example.haus.repository;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.response.statistic.BestSellerRow;
import com.example.haus.domain.entity.product.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(
        """
        SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END 
                FROM OrderItem oi 
                        WHERE oi.order.user.id = :userId
                                AND oi.productVariation.product.id = :productId
                                        AND oi.order.status = :status
        """
    )
    boolean existsByUserIdAndProductIdAndOrderStatus(
        @Param("userId") String userId,
        @Param("productId") Long productId,
        @Param("status") OrderStatus status
    );
    @Query(
            """
            SELECT oi 
                    FROM OrderItem oi 
                            WHERE oi.order.user.id = :userId
                                    AND oi.productVariation.product.id = :productId
                                            AND oi.order.status = :status
                            ORDER BY oi.createdAt DESC
            """
    )
    List<OrderItem> findByUserIdAndProductIdAndOrderStatus(
            @Param("userId") String userId,
            @Param("productId") Long productId,
            @Param("status") OrderStatus status
    );

    @Query("""
            SELECT p.id as productId, SUM(oi.quantity) as soldQuantity
            FROM OrderItem oi
            JOIN oi.productVariation pv
            JOIN oi.order o
            JOIN pv.product p
            WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate
            GROUP BY p.id
            ORDER BY SUM(oi.quantity) DESC
            """)
    Page<BestSellerRow> findBestSellers(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);


}
