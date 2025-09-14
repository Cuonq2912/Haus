package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.dto.request.product.CreateProductVariationRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductVariationRequestDto;
import com.example.haus.domain.dto.response.product.ProductVariationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring", 
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductVariationMapper {

        List<ProductVariationResponseDto> toListProductVariationResponseDto(List<ProductVariation> productVariations);

        ProductVariationResponseDto toProductVariationResponseDto(ProductVariation productVariation);

        ProductVariation toProductVariation(CreateProductVariationRequestDto request);

        void updateProductVariationFromDto(
                        @MappingTarget ProductVariation productVariation,
                        UpdateProductVariationRequestDto request);
}
