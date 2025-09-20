package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.CreateProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto getProductById(Long id);

    ProductResponseDto createProduct(CreateProductRequestDto request);

    ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request);

    void deleteProduct(Long productId);

    PaginationResponseDto<ProductResponseDto> getProductsByCategoryId(Long categoryId,
            PaginationRequestDto paginationRequest);

    PaginationResponseDto<ProductResponseDto> filterProducts(
            PaginationRequestDto paginationRequest, String sortByPrice, String... search);

}
