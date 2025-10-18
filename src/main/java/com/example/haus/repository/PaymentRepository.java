package com.example.haus.repository;

import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentGateway;
import com.example.haus.domain.entity.product.payment.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByOrderIdAndGateway(@Param("orderId") Long orderId, @Param("gateway") PaymentGateway gateway);

    Optional<Payment> findByOrderIdAndType(@Param("orderId") Long orderId, @Param("type") PaymentType type);
}
