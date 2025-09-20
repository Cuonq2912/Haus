package com.example.haus.service.impl;

import com.example.haus.constant.AppConstants;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.mapper.CategoryMapper;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.service.CategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    CategoryMapper categoryMapper;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequest) {

        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_EXISTED);
        }

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

    @Override
    public PaginationResponseDto<CategoryResponseDto> getAllCategories(PaginationRequestDto requestDto) {
        Pageable pageable = PageRequest.of(requestDto.getPageNum(), requestDto.getPageSize());

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponseDto> categoryResponseDtoList = categoryPage.getContent().stream()
                .map(categoryMapper::categoryToCategoryResponseDto)
                .toList();

        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(requestDto.getPageNum() + 1)
                .pageSize(requestDto.getPageSize())
                .totalElement(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .build();

        return new PaginationResponseDto<>(paginationCustom, categoryResponseDtoList);
    }

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

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<CategoryResponseDto> searchCategoryByKeywordAndSortByKeyword(
            String keyword,
            String sort,
            PaginationRequestDto paginationRequest) {

        int page = paginationRequest.getPageNum();
        int size = paginationRequest.getPageSize();

        StringBuilder jpql = new StringBuilder("SELECT c FROM Category c WHERE TRUE");

        if (StringUtils.hasLength(keyword)) {
            jpql.append(" AND ( lower(c.categoryName) LIKE lower(:keyword) ");
            jpql.append(" OR lower(c.description) LIKE lower(:keyword) )");
        }

        // xử lý sort (validate trước)
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile(AppConstants.SORT_BY); // ví dụ sort=name:asc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.matches()) {
                jpql.append(String.format(" ORDER BY c.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        TypedQuery<Category> query = entityManager.createQuery(jpql.toString(), Category.class);
        if (StringUtils.hasLength(keyword)) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Category> categories = query.getResultList();

        // đếm total
        StringBuilder jpqlCount = new StringBuilder("SELECT COUNT(c) FROM Category c WHERE TRUE");
        if (StringUtils.hasLength(keyword)) {
            jpqlCount.append(" AND ( lower(c.categoryName) LIKE lower(:keyword) ");
            jpqlCount.append(" OR lower(c.description) LIKE lower(:keyword) )");
        }
        TypedQuery<Long> countQuery = entityManager.createQuery(jpqlCount.toString(), Long.class);
        if (StringUtils.hasLength(keyword)) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        long totalElements = countQuery.getSingleResult();

        // phân trang
        Pageable pageable = PageRequest.of(page, size);
        List<CategoryResponseDto> categoryDtos = categories.stream()
                .map(categoryMapper::categoryToCategoryResponseDto)
                .toList();

        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalElement(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .build();

        return new PaginationResponseDto<>(paginationCustom, categoryDtos);
    }

}
