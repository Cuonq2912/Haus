package com.example.haus.repository;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.productVariation pv " +
            "LEFT JOIN FETCH pv.product p " +
            "LEFT JOIN FETCH pv.media " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderItems(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.productVariation pv " +
            "LEFT JOIN FETCH pv.product p " +
            "LEFT JOIN FETCH pv.media")
    Page<Order> findAllWithOrderItems(Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.payment p " +
            "LEFT JOIN FETCH o.promotion prom " +
            "WHERE o.id = :orderId")
    Optional<Order> findOrderDetailsForInvoice(@Param("orderId") Long orderId);


    @Query("""
        SELECT o FROM Order o
        WHERE (FLOOR((MONTH(o.orderDate) - 1) / 3) + 1) = :quarter
          AND YEAR(o.orderDate) = :year
          AND ((o.orderDate >= :startDate) AND (o.orderDate <= :endDate))
    """)
    List<Order> findByQuarter(@Param("quarter") int quarter,
                              @Param("year") int year,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);


    Optional<Order> findByOrderNumber(String orderNumber);

    @Query(
            """
            SELECT p
            FROM Order p
            WHERE (p.orderDate >= :startDate) AND (p.orderDate <= :endDate)
            """
    )
    Page<Order> findByDateTime(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId, String userId);
}