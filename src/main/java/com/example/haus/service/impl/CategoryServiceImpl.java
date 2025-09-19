package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.mapper.CategoryMapper;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequest) {
        Category category = categoryMapper.categoryRequestDtoToCategory(categoryRequest);

        // Nếu có parentId thì set parentCategory
        if (categoryRequest.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }
        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));

        categoryMapper.updateCategoryFromDto(categoryRequest, category);

        if (categoryRequest.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryResponseDto(saved);
    }


    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
        return categoryMapper.categoryToCategoryResponseDto(category);
    }

//    @Override
//    public PaginationResponseDto<CategoryResponseDto> getAllCategories(PaginationRequestDto requestDto) {
//
//
//    }

    @Override
    public List<CategoryResponseDto> getAllSubCategories() {
        List<Category> categories = categoryRepository.findByParentCategoryIsNotNull();
        return categories.stream().map(category -> categoryMapper.categoryToCategoryResponseDto(category)).toList();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
    
        if (!category.getProducts().isEmpty()) {
            throw new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_BEING_USED);
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDto getCategoryByCategoryName(String categoryName) {
        Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
        return categoryMapper.categoryToCategoryResponseDto(category);
    }
}
