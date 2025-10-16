package com.example.haus.repository;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
