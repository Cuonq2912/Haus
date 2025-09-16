package com.example.haus.repository.custom;

import com.example.haus.domain.dto.request.product.ProductFilterRequestDto;
import com.example.haus.domain.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {
  Page<Product> filterProducts(ProductFilterRequestDto filterRequest, Pageable pageable);
}