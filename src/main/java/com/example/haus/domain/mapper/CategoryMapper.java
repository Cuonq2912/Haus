package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.entity.product.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CategoryMapper {

    @Mapping(target = "parentCategory", ignore = true)
    Category categoryRequestDtoToCategory(CategoryRequestDto requestDto);

    void updateCategoryFromDto(CategoryRequestDto requestDto, @MappingTarget Category category);

    CategoryResponseDto categoryToCategoryResponseDto(Category category);
}
