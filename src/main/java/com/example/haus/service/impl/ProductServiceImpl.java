package com.example.haus.service.impl;

import com.example.haus.constant.AppConstants;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.entity.product.Category;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.mapper.ProductMapper;
import com.example.haus.domain.dto.request.product.CreateProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.criteria.SearchCriteria;
import com.example.haus.repository.criteria.SearchQueryCriteriaConsumer;
import com.example.haus.service.ProductService;
import com.example.haus.util.ProductCodeUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    ProductMapper productMapper;

    CategoryRepository categoryRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (product.getIsDeleted())
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_ALREADY_DELETED);

        return productMapper.toProductResponseDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(CreateProductRequestDto request) {

        if (productRepository.existsByProductNameAndIsDeletedFalse(request.getProductName())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        Product product = productMapper.createProductRequestDtoToProduct(request);

        String productCode;
        do {
            productCode = ProductCodeUtil.generateProductCode();
        } while (productRepository.existsByProductCode(productCode));
        product.setProductCode(productCode);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
            product.addCategory(category);
        }

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponseDto(savedProduct);
    }


    @Override
    public ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request) {
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (request.getProductName() != null &&
                !product.getProductName().equals(request.getProductName()) &&
                productRepository.existsByProductNameAndIsDeletedFalse(request.getProductName())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        productMapper.updateProductFromDto(request, product);

        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            Category category = categoryRepository.findByCategoryNameIgnoreCase(request.getCategory())
                    .orElseThrow(() -> new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));

            product.getCategories().add(category);
        }

        product.setUpdatedAt(new Date());

        Product updatedProduct = productRepository.save(product);

        return productMapper.toProductResponseDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        product.setIsDeleted(CommonConstant.TRUE);

    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ProductResponseDto> getProductsByCategoryId(Long categoryId,
            PaginationRequestDto paginationRequest) {
        if (categoryId == null || categoryId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize());

        Page<Product> productsPage = productRepository.findProductsByCategoryId(categoryId, pageable);

        List<ProductResponseDto> productResponseList = productsPage.getContent().stream()
                .map(productMapper::toProductResponseDto)
                .toList();

        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(paginationRequest.getPageNum() + 1)
                .pageSize(paginationRequest.getPageSize())
                .totalElement(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .build();

        return new PaginationResponseDto<>(paginationCustom, productResponseList);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ProductResponseDto> filterProducts(PaginationRequestDto paginationRequest,
                                                                    String sortByPrice,
                                                                    String... search) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        if(search.length > 0) {
            Pattern pattern = Pattern.compile(AppConstants.SEARCH_OPERATOR);
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    searchCriteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<Product> products = getProducts(paginationRequest.getPageNum(), paginationRequest.getPageSize(), searchCriteriaList,  sortByPrice);

        Long totalElements = getTotalElements(searchCriteriaList);

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize());

        Page<Product> pages = new PageImpl<>(products, pageable, totalElements);

        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(paginationRequest.getPageNum())
                .pageSize(paginationRequest.getPageSize())
                .totalElement(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .sortType(sortByPrice)
                .sortBy(sortByPrice != null ? "price" : null)
                .build();

        List<ProductResponseDto> productResponseDtoList = pages.getContent().stream()
                .map(product -> productMapper.toProductResponseDto(product))
                .toList();

        return PaginationResponseDto.<ProductResponseDto>builder()
                .pageCustom(paginationCustom)
                .items(productResponseDtoList)
                .build();
    }

    private List<Product> getProducts(int page, int size, List<SearchCriteria> searchCriteriaList, String sortByPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Product> consumer = new SearchQueryCriteriaConsumer(predicate, cb, root);

        // Áp dụng tất cả search criteria
        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        query.where(predicate);

        // Sort theo giá
        if ("asc".equalsIgnoreCase(sortByPrice)) {
            query.orderBy(cb.asc(root.get("price")));
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            query.orderBy(cb.desc(root.get("price")));
        }

        return entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }


    private Long getTotalElements(List<SearchCriteria> searchCriteriaList) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> root = countQuery.from(Product.class);

        // Join với productVariants để filter giống truy vấn chính
        Join<Product, ProductVariation> variantsJoin = root.join("productVariations", JoinType.LEFT);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Product> consumer = new SearchQueryCriteriaConsumer(predicate, cb, root);
        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        // Nếu có filter theo variant (ví dụ màu sắc)
        if (searchCriteriaList.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("color"))) {
            List<String> colorValues = searchCriteriaList.stream()
                    .filter(c -> c.getKey().equalsIgnoreCase("color"))
                    .map(SearchCriteria::getValue)
                    .map(Object::toString)
                    .toList();
            predicate = cb.and(predicate, variantsJoin.get("color").in(colorValues));
        }

        countQuery.select(cb.countDistinct(root));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }


}
