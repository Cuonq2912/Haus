package com.example.haus.domain.mapper;

import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.response.product.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductMapper {

    ProductResponseDto productToProductResponseDto (Product product);
}
