package com.example.haus.repository;

import com.example.haus.domain.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductName(String productName);

    Optional<Product> findByProductId(Long id);
}
