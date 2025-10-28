package com.example.haus.service;

import com.example.haus.domain.dto.request.product.CreateProductVariationRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductVariationRequestDto;
import com.example.haus.domain.dto.response.product.ProductVariationResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductVariationService {

    List<ProductVariationResponseDto> getProductVariationsByProductId(Long productId);

    ProductVariationResponseDto getProductVariationById(Long productVariationId);

    ProductVariationResponseDto createProductVariation(CreateProductVariationRequestDto request, MultipartFile image);

    ProductVariationResponseDto updateProductVariation(Long productVariantId, UpdateProductVariationRequestDto request, MultipartFile image);

    void deleteProductVariation(Long productVariationId);

}
