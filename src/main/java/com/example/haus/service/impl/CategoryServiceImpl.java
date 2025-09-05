package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.mapper.CategoryMapper;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequest) {
        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_EXISTED);
        }
        Category category = categoryMapper.categoryRequestDtoToCategory(categoryRequest);
        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequest) {
        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_EXISTED);
        }
        Category category = categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
        categoryMapper.categoryRequestDtoToCategory(categoryRequest);
        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return null;
    }

    @Override
    public List<CategoryResponseDto> getAllCategory() {
        return List.of();
    }

    @Override
    public String deleteCategory(Long id) {
        return "";
    }

    @Override
    public CategoryResponseDto getCategoryByCategoryName(String categoryName) {
        return null;
    }
}
