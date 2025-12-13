package com.example.haus.repository;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
