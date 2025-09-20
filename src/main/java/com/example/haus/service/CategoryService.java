package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto addCategory(CategoryRequestDto categoryRequest);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequest);

    CategoryResponseDto getCategoryById(Long id);

    PaginationResponseDto<CategoryResponseDto> getAllCategories(PaginationRequestDto requestDto);

    List<CategoryResponseDto> getAllSubCategories();

    void deleteCategory(Long id);

    CategoryResponseDto getCategoryByCategoryName(String categoryName);

    PaginationResponseDto<CategoryResponseDto> searchCategoryByKeywordAndSortByKeyword(String keyword, String search,
                                                                      PaginationRequestDto paginationRequest);


}
