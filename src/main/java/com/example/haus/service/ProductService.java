package com.example.haus.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.request.product.ProductFilterRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto getProductById(Long id);

    PaginationResponseDto<ProductResponseDto> getAllProducts(PaginationRequestDto paginationRequest);

    ProductResponseDto createProduct(ProductRequestDto request, MultipartFile[] images);

    ProductResponseDto updateProduct(Long productId, ProductRequestDto request, MultipartFile[] images);

    void deleteProduct(Long productId);

    PaginationResponseDto<ProductResponseDto> getProductsByCategory(String categoryName,
            PaginationRequestDto paginationRequest);

    PaginationResponseDto<ProductResponseDto> getProductsByCategoryId(Long categoryId,
            PaginationRequestDto paginationRequest);

    PaginationResponseDto<ProductResponseDto> searchProductsByKeyword(String keyword,
            PaginationRequestDto paginationRequest);

    PaginationResponseDto<ProductResponseDto> filterProducts(ProductFilterRequestDto filterRequest,
            PaginationRequestDto paginationRequest);

}
