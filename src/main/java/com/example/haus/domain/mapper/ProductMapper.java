package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.dto.request.product.CreateProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    @Mapping(target = "categoryId", expression = "java(product.getCategories() != null && !product.getCategories().isEmpty() ? product.getCategories().get(0).getId() : null)")
    ProductResponseDto toProductResponseDto(Product product);

    Product createProductRequestDtoToProduct(CreateProductRequestDto request);

    void updateProductFromDto(UpdateProductRequestDto request, @MappingTarget Product product);
}
