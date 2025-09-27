package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class, MediaMapper.class}
)
public interface ProductMapper {
    ProductResponseDto productToProductResponse(Product product);

    @Mapping(target = "categories", ignore = true)
    Product createProductRequestDtoToProduct(ProductRequestDto request);

    @Mapping(target = "categories", ignore = true)
    void updateProductFromDto(ProductRequestDto request, @MappingTarget Product product);

}
