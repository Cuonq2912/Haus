package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.pagination.PaginationCustom;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<CategoryResponseDto> getAllSubCategories() {
        List<Category> categories = categoryRepository.findByParentCategoryIsNotNull();
        return categories.stream().map(category -> categoryMapper.categoryToCategoryResponseDto(category)).toList();
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
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
    @Transactional(readOnly = true)
    public PaginationResponseDto<CategoryResponseDto> searchCategoryByKeywordAndSortByKeyword(
            String keyword,
            PaginationRequestDto paginationRequest) {

        // Pageable của Spring bắt đầu từ 0
        int page = paginationRequest.getPageNum();
        int size = paginationRequest.getPageSize();

        // 1. Query category cha (parentCategory IS NULL)
        String jpqlParent = "SELECT c FROM Category c WHERE c.parentCategory IS NULL";
        TypedQuery<Category> queryParent = entityManager.createQuery(jpqlParent, Category.class);
        queryParent.setFirstResult(0);
        queryParent.setMaxResults(8);
        List<Category> parentCategories = queryParent.getResultList();

        // 1.1. Query count của category con (parentCategory NOT NULL, theo keyword)
        StringBuilder jpqlChildCount = new StringBuilder("SELECT COUNT(c) FROM Category c WHERE c.parentCategory IS NOT NULL");
        if (StringUtils.hasLength(keyword)) {
            jpqlChildCount.append(" AND lower(c.categoryName) LIKE lower(:keyword) ");
            jpqlChildCount.append(" OR lower(c.description) LIKE lower(:keyword) ");
        }
        TypedQuery<Long> countQuery = entityManager.createQuery(jpqlChildCount.toString(), Long.class);
        if (StringUtils.hasLength(keyword)) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        long totalElements = countQuery.getSingleResult();

        // 2. Query category con (parentCategory NOT NULL, theo keyword)
        StringBuilder jpqlChild = new StringBuilder("SELECT c FROM Category c WHERE c.parentCategory IS NOT NULL");
        if (StringUtils.hasLength(keyword)) {
            jpqlChild.append(" AND lower(c.categoryName) LIKE lower(:keyword) ");
            jpqlChild.append(" OR lower(c.description) LIKE lower(:keyword) ");
        }
        TypedQuery<Category> queryChild = entityManager.createQuery(jpqlChild.toString(), Category.class);
        if (StringUtils.hasLength(keyword)) {
            queryChild.setParameter("keyword", "%" + keyword + "%");
        }
        queryChild.setFirstResult(page * size);
        queryChild.setMaxResults(size);
        List<Category> childCategories = queryChild.getResultList();

        // 3. Map parentCategories sang DTO
        List<CategoryResponseDto> categoryDtos = parentCategories.stream()
                .map(categoryMapper::categoryToCategoryResponseDto)
                .toList();

        // Tạo map parentId -> DTO
        Map<Long, CategoryResponseDto> parentMap = categoryDtos.stream()
                .collect(Collectors.toMap(CategoryResponseDto::getId, dto -> {
                    dto.getSubCategories().clear();
                    return dto;
                }));

        // 4. Gán con vào parent
        for (Category child : childCategories) {
            if (child.getParentCategory() != null) {
                Long parentId = child.getParentCategory().getId();
                CategoryResponseDto parentDto = parentMap.get(parentId);
                if (parentDto != null) {
                    parentDto.getSubCategories().add(categoryMapper.categoryToCategoryResponseDto(child));
                }
            }
        }

        // 5. Build pagination
        int totalPages = (int) Math.ceil((double) totalElements / size);

        log.info("page = {}; size = {}", page, size);

        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(page + 1)
                .pageSize(size)
                .totalElement(totalElements)
                .totalPages(totalPages)
                .build();

        return new PaginationResponseDto<>(paginationCustom, categoryDtos);
    }

}
