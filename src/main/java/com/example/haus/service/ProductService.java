package com.example.haus.service;


import com.example.haus.domain.request.product.CreateProductRequestDto;
import com.example.haus.domain.request.product.UpdateProductRequestDto;
import com.example.haus.domain.response.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto getProductById(Long id);

    //get all

    ProductResponseDto createProduct(CreateProductRequestDto request);

    ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request);

    void deleteProduct(Long productId);
}
