package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.dto.request.product.CreateProductVariationRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductVariationRequestDto;
import com.example.haus.domain.dto.response.product.ProductVariationResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring", 
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductVariationMapper {

        List<ProductVariationResponseDto> toListProductVariationResponseDto(List<ProductVariation> productVariations);

        @Mapping(target = "discountPercent",
                expression = "java(productVariation.getProduct()" +
                        "     .getCategories().stream()" +
                        "    .map(com.example.haus.domain.entity.product.Category::getPromotion)" +
                        "    .filter(java.util.Objects::nonNull)" +
                        "    .map(com.example.haus.domain.entity.product.Promotion::getDiscountPercent)" +
                        "    .filter(java.util.Objects::nonNull)" +
                        "    .max(java.util.Comparator.naturalOrder())" +
                        "    .orElse(0.0f))")
        @Mapping(target = "daysRemaining",
                expression = "java(productVariation.getProduct().getCategories().stream()" +
                        "    .map(com.example.haus.domain.entity.product.Category::getPromotion)" +
                        "    .filter(java.util.Objects::nonNull)" +
                        "    .filter(p -> p.getEndDate() != null && p.getEndDate().isAfter(java.time.LocalDate.now()))" +
                        "    .max(java.util.Comparator.comparing(com.example.haus.domain.entity.product.Promotion::getDiscountPercent))" +
                        "    .map(com.example.haus.domain.entity.product.Promotion::getEndDate)" +
                        "    .map(ld -> java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), ld))" +
                        "    .orElse(0L))")
        ProductVariationResponseDto toProductVariationResponseDto(ProductVariation productVariation);

        ProductVariation toProductVariation(CreateProductVariationRequestDto request);

        void updateProductVariationFromDto(
                        @MappingTarget ProductVariation productVariation,
                        UpdateProductVariationRequestDto request);
}
