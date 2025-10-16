package com.example.haus.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;

public interface ProductService {

    ProductResponseDto getProductById(Long id);

    PaginationResponseDto<ProductResponseDto> getAllProducts(PaginationRequestDto paginationRequest);

    ProductResponseDto createProduct(ProductRequestDto request, MultipartFile[] images);

    ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request, MultipartFile[] images);

    void deleteProduct(Long productId);

    PaginationResponseDto<ProductResponseDto> getProductsByCategoryId(Long categoryId,
                                                                      PaginationRequestDto paginationRequest);

    PaginationResponseDto<ProductResponseDto> filterProducts(
            PaginationRequestDto paginationRequest, String sortBy, String search);

}
