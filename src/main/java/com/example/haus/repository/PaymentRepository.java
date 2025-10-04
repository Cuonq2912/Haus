package com.example.haus.repository;

import com.example.haus.domain.entity.product.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
