package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.entity.product.Category;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CategoryMapper {

    Category categoryRequestDtoToCategory(CategoryRequestDto requestDto);

    CategoryResponseDto categoryToCategoryResponseDto(Category category);
}
