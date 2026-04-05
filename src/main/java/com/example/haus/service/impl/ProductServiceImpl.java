package com.example.haus.service.impl;

import com.example.haus.constant.AppConstants;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.MediaType;
import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import com.example.haus.domain.entity.product.*;
import com.example.haus.domain.mapper.ProductMapper;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.MediaRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.criteria.SearchCriteria;
import com.example.haus.repository.criteria.SearchQueryCriteriaConsumer;
import com.example.haus.service.ProductService;
import com.example.haus.util.PaginationUtil;
import com.example.haus.util.ProductCodeUtil;
import com.example.haus.util.UploadFileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.haus.constant.CommonConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    ProductMapper productMapper;

    CategoryRepository categoryRepository;

    UploadFileUtil uploadFileUtil;

    MediaRepository mediaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findByIdWithActiveVariations(id);

        if (product == null)
            throw new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED);

        if (product.getIsDeleted().equals(CommonConstant.TRUE))
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_ALREADY_DELETED);

        return productMapper.productToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ProductResponseDto> getAllProducts(PaginationRequestDto paginationRequest) {
        if (paginationRequest == null) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Sort sort = Sort.by(
                paginationRequest.getSortType().equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                paginationRequest.getSortBy());

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize(), sort);

        Page<Product> productsPage = productRepository.findAllActiveProducts(pageable);

        List<ProductResponseDto> productResponseList = productsPage.getContent().stream()
                .map(productMapper::productToProductResponse).toList();

        return PaginationUtil.createPaginationResponse(productsPage, paginationRequest, productResponseList);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request, MultipartFile[] images) {

        if (Boolean.TRUE.equals(productRepository.existsByProductNameAndIsDeletedFalse(request.getProductName()))) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        Product product = productMapper.createProductRequestDtoToProduct(request);

        String productCode;
        do {
            productCode = ProductCodeUtil.generateProductCode();
        } while (Boolean.TRUE.equals(productRepository.existsByProductCode(productCode)));

        product.setProductCode(productCode);

        Date now = new Date();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        if (product.getInventoryQuantity() == null) {
            product.setInventoryQuantity(0);
        }

        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            for (String categoryName : request.getCategories()) {
                Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                        .orElseThrow(() -> new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));

                product.addCategory(category);
            }
        }

        Product savedProduct = productRepository.save(product);

        if (images != null && images.length > 0) {
            List<MultipartFile> imageList = List.of(images);
            List<String> imageUrls = uploadFileUtil.uploadMultipleFiles(imageList);

            for (String imageUrl : imageUrls) {
                Media media = Media.builder().url(imageUrl).type(MediaType.IMAGE).product(savedProduct).build();
                media.setCreatedAt(now);
                media.setUpdatedAt(now);

                if (savedProduct.getMedias() == null) {
                    savedProduct.setMedias(new HashSet<>());
                }
                savedProduct.getMedias().add(media);
            }

            savedProduct = productRepository.save(savedProduct);
        }

        return productMapper.productToProductResponse(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request, MultipartFile[] images) {
        validateProductId(productId);

        Product product = getActiveProductOrThrow(productId);

        validateUniqueProductName(product, request);

        productMapper.updateProductFromUpdateDto(request, product);

        updateCategoriesIfPresent(product, request);

        deleteImagesIfRequested(product, productId, request);

        addNewImagesIfPresent(product, images);

        touchUpdatedAt(product);

        Product updatedProduct = productRepository.save(product);
        return productMapper.productToProductResponse(updatedProduct);
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }
    }

    private Product getActiveProductOrThrow(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (CommonConstant.TRUE.equals(product.getIsDeleted())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_ALREADY_DELETED);
        }
        return product;
    }

    private void validateUniqueProductName(Product product, UpdateProductRequestDto request) {
        String newName = request.getProductName();
        if (newName == null)
            return;

        boolean isChanged = !product.getProductName().equals(newName);
        boolean exists = productRepository.existsByProductNameAndIsDeletedFalse(newName);

        if (isChanged && exists) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }
    }

    private void updateCategoriesIfPresent(Product product, UpdateProductRequestDto request) {
        if (request.getCategories() == null || request.getCategories().isEmpty())
            return;

        product.getCategories().clear();
        for (String categoryName : request.getCategories()) {
            Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                    .orElseThrow(() -> new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
            product.addCategory(category);
        }
    }

    private void deleteImagesIfRequested(Product product, Long productId, UpdateProductRequestDto request) {
        if (request.getImageIdsToDelete() == null || request.getImageIdsToDelete().isEmpty())
            return;

        List<Media> mediasToDelete = mediaRepository.findByIdsAndProductId(request.getImageIdsToDelete(), productId);

        for (Media mediaToDelete : mediasToDelete) {
            deleteMediaSafely(product, mediaToDelete);
        }
    }

    private void deleteMediaSafely(Product product, Media mediaToDelete) {
        try {
            uploadFileUtil.destroyFileWithUrl(mediaToDelete.getUrl());
            product.getMedias().remove(mediaToDelete);
            mediaRepository.delete(mediaToDelete);
        } catch (Exception e) {
            log.warn("Failed to delete media with ID {} from cloud storage: {}", mediaToDelete.getId(), e.getMessage(),
                    e);
        }
    }

    private void addNewImagesIfPresent(Product product, MultipartFile[] images) {
        if (images == null || images.length == 0)
            return;

        List<String> newImageUrls = uploadFileUtil.uploadMultipleFiles(List.of(images));
        Date now = new Date();

        if (product.getMedias() == null) {
            product.setMedias(new HashSet<>());
        }

        for (String imageUrl : newImageUrls) {
            Media media = Media.builder().url(imageUrl).type(MediaType.IMAGE).product(product).build();
            media.setCreatedAt(now);
            media.setUpdatedAt(now);

            product.getMedias().add(media);
        }
    }

    private void touchUpdatedAt(Product product) {
        product.setUpdatedAt(new Date());
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
            PaginationRequestDto paginationRequest, String sortBy, String search) {
        if (categoryId == null || categoryId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Page<Product> productsPage = getProductsPageByFilter(categoryId, paginationRequest, sortBy, search);

        List<ProductResponseDto> productResponseList = productsPage.getContent().stream()
                .map(productMapper::productToProductResponse).toList();

        return PaginationUtil.createPaginationResponse(productsPage, paginationRequest, productResponseList);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ProductResponseDto> filterProducts(PaginationRequestDto paginationRequest,
            String sortBy, String search) {
        log.info("Sorting by: {}; Search query: {}", sortBy, search);

        PageImpl<Product> productsPage = getProductsPageByFilter(null, paginationRequest, sortBy, search);

        List<ProductResponseDto> productResponseDtoList = productsPage.getContent().stream()
                .map(productMapper::productToProductResponse).toList();

        PaginationCustom paginationCustom = createPagination(paginationRequest, sortBy, productsPage);

        return PaginationResponseDto.<ProductResponseDto> builder().pageCustom(paginationCustom)
                .items(productResponseDtoList).build();
    }

    private PageImpl<Product> getProductsPageByFilter(Long categoryId, // Tham số categoryId
            PaginationRequestDto paginationRequest, String sortBy, String search) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            String[] newSearch = StringUtils.split(search, "&");
            Pattern pattern = Pattern.compile(AppConstants.SEARCH_OPERATOR);
            for (String s : newSearch) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    searchCriteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<Product> products = getProducts(categoryId, paginationRequest, searchCriteriaList, sortBy);
        Long totalElements = getTotalElements(categoryId, searchCriteriaList);

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize());

        return new PageImpl<>(products, pageable, totalElements);
    }

    private PaginationCustom createPagination(PaginationRequestDto paginationRequest, String sortBy,
            PageImpl<?> pages) {
        return PaginationCustom.builder().pageNum(paginationRequest.getPageNum() + 1).pageSize(pages.getSize())
                .totalElement(pages.getTotalElements()).totalPages(pages.getTotalPages()).sortType(sortBy)
                .sortBy(determineSortByField(sortBy)).build();
    }

    private List<Product> getProducts(Long categoryId, PaginationRequestDto requestDto,
            List<SearchCriteria> searchCriteriaList, String sortBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Product> consumer = new SearchQueryCriteriaConsumer<>(predicate, cb, root);

        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        if (categoryId != null) {
            Join<Product, Category> categoryJoin = root.join(CATEGORIES, JoinType.INNER);
            Predicate categoryPredicate = cb.equal(categoryJoin.get(ID), categoryId);
            predicate = cb.and(predicate, categoryPredicate);
        }

        Predicate deletedPredicate = cb.equal(root.get(IS_DELETED), false);
        predicate = cb.and(predicate, deletedPredicate);

        query.where(predicate);

        if (sortBy != null) {
            if (ASC.equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.asc(root.get(PRICE)));
            } else if (DESC.equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.desc(root.get(PRICE)));
            } else if (SOLD_QUANTITY_ASC.equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.asc(root.get(SOLD_QUANTITY)));
            } else if (SOLD_QUANTITY_DESC.equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.desc(root.get(SOLD_QUANTITY)));
            } else if (CREATED_AT_ASC.equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.asc(root.get(CREATED_AT)));
            } else if (CREATED_AT_DESC.equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.desc(root.get(CREATED_AT)));
            } else if (DISCOUNT_ASC.equalsIgnoreCase(sortBy) || DISCOUNT_ASC.equalsIgnoreCase(sortBy)) {
                Join<Product, Category> categoryJoin = root.join(CATEGORIES, JoinType.LEFT);
                Join<Category, Promotion> promotionJoin = categoryJoin.join(PROMOTION, JoinType.LEFT);

                if (DISCOUNT_ASC.equalsIgnoreCase(sortBy)) {
                    query.orderBy(cb.asc(promotionJoin.get(DISCOUNT_PERCENT)));
                } else {
                    query.orderBy(cb.desc(promotionJoin.get(DISCOUNT_PERCENT)));
                }
            }
        }

        return entityManager.createQuery(query).setFirstResult(requestDto.getPageNum() * requestDto.getPageSize())
                .setMaxResults(requestDto.getPageSize()).getResultList();
    }

    private Long getTotalElements(Long categoryId, List<SearchCriteria> searchCriteriaList) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> root = countQuery.from(Product.class);

        Join<Product, ProductVariation> variantsJoin = root.join(PRODUCT_VARIATIONS, JoinType.LEFT);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Product> consumer = new SearchQueryCriteriaConsumer<>(predicate, cb, root);
        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        if (categoryId != null) {
            Join<Product, Category> categoryJoin = root.join(CATEGORIES, JoinType.INNER);
            Predicate categoryPredicate = cb.equal(categoryJoin.get(ID), categoryId);
            predicate = cb.and(predicate, categoryPredicate);
        }

        Predicate deletedPredicate = cb.equal(root.get(IS_DELETED), false);
        predicate = cb.and(predicate, deletedPredicate);

        if (searchCriteriaList.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("color"))) {
            List<String> colorValues = searchCriteriaList.stream().filter(c -> c.getKey().equalsIgnoreCase(COLOR))
                    .map(SearchCriteria::getValue).map(Object::toString).toList();
            predicate = cb.and(predicate, variantsJoin.get(COLOR).in(colorValues));
        }

        countQuery.select(cb.countDistinct(root));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private String determineSortByField(String sortBy) {
        if (sortBy == null) {
            return null;
        } else if (ASC.equalsIgnoreCase(sortBy) || DESC.equalsIgnoreCase(sortBy)) {
            return PRICE;
        } else if (DISCOUNT_ASC.equalsIgnoreCase(sortBy) || DISCOUNT_DESC.equalsIgnoreCase(sortBy)) {
            return DISCOUNT_PERCENT;
        } else if (SOLD_QUANTITY_ASC.equalsIgnoreCase(sortBy) || SOLD_QUANTITY_DESC.equalsIgnoreCase(sortBy)) {
            return SOLD_QUANTITY;
        } else if (CREATED_AT_ASC.equalsIgnoreCase(sortBy) || CREATED_AT_DESC.equalsIgnoreCase(sortBy)) {
            return CREATED_AT;
        }
        return null;
    }

}
