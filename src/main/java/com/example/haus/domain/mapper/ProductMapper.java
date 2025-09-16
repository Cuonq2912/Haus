package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.dto.request.product.CreateProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    ProductResponseDto toProductResponseDto(Product product);

    Product createProductRequestDtoToProduct(CreateProductRequestDto request);

    void updateProductFromDto(UpdateProductRequestDto request, @MappingTarget Product product);
}
