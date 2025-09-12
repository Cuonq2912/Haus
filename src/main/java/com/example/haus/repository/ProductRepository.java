package com.example.haus.repository;

import com.example.haus.domain.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByProductNameAndIsDeletedFalse(String name);

    Boolean existsByProductNameAndIsDeletedTrue(String name);

}
