package com.example.haus.service;

import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto addCategory(CategoryRequestDto categoryRequest);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequest);

    CategoryResponseDto getCategoryById(Long id);

    List<CategoryResponseDto> getAllCategory();

    String deleteCategory(Long id);

    CategoryResponseDto getCategoryByCategoryName(String categoryName);

}
