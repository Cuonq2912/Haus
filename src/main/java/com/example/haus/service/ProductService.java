package com.example.haus.service;

import com.example.haus.domain.dto.response.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto addProduct(ProductRequestDto request);
}
