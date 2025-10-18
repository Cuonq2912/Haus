package com.example.haus.repository;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.payment p " +
            "LEFT JOIN FETCH o.promotion prom " +
            "WHERE o.id = :orderId")
    Optional<Order> findOrderDetailsForInvoice(@Param("orderId") Long orderId);

    // Có thể thêm các phương thức khác tại đây, ví dụ:
    // Optional<Order> findByOrderNumber(String orderNumber);
}