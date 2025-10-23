package com.example.haus.service.impl;

import com.example.haus.constant.AppConstants;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.MediaType;
import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.entity.product.*;
import com.example.haus.domain.mapper.ProductMapper;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CategoryRepository;
import com.example.haus.repository.MediaRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.criteria.SearchCriteria;
import com.example.haus.repository.criteria.SearchQueryCriteriaConsumer;
import com.example.haus.service.ProductService;
import com.example.haus.util.ProductCodeUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import com.example.haus.util.PaginationUtil;
import com.example.haus.util.UploadFileUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.multipart.MultipartFile;

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

        if(product == null)
            throw new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED);

        if (product.getIsDeleted() == CommonConstant.TRUE)
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
                paginationRequest.getSortType().equalsIgnoreCase("DESC")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                paginationRequest.getSortBy());

        Pageable pageable = PageRequest.of(
                paginationRequest.getPageNum(),
                paginationRequest.getPageSize(),
                sort);

        Page<Product> productsPage = productRepository.findAllActiveProducts(pageable);

        List<ProductResponseDto> productResponseList = productsPage.getContent().stream()
                .map(productMapper::productToProductResponse)
                .toList();

        return PaginationUtil.createPaginationResponse(productsPage, paginationRequest, productResponseList);
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request, MultipartFile[] images) {

        if (productRepository.existsByProductNameAndIsDeletedFalse(request.getProductName())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        Product product = productMapper.createProductRequestDtoToProduct(request);

        String productCode;
        do {
            productCode = ProductCodeUtil.generateProductCode();
        } while (productRepository.existsByProductCode(productCode));

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
                Media media = Media.builder()
                        .url(imageUrl)
                        .type(MediaType.Image)
                        .product(savedProduct)
                        .build();
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
        if (productId == null || productId <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (product.getIsDeleted() == CommonConstant.TRUE) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_ALREADY_DELETED);
        }

        if (request.getProductName() != null &&
                !product.getProductName().equals(request.getProductName()) &&
                productRepository.existsByProductNameAndIsDeletedFalse(request.getProductName())) {
            throw new InvalidDataException(ErrorMessage.Product.ERR_PRODUCT_NAME_EXISTED);
        }

        productMapper.updateProductFromUpdateDto(request, product);

        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            product.getCategories().clear();
            for (String categoryName : request.getCategories()) {
                Category category = categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                        .orElseThrow(() -> new InvalidDataException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));

                product.addCategory(category);
            }
        }

        if (request.getImageIdsToDelete() != null && !request.getImageIdsToDelete().isEmpty()) {
            List<Media> mediasToDelete = mediaRepository.findByIdsAndProductId(
                    request.getImageIdsToDelete(), productId);

            for (Media mediaToDelete : mediasToDelete) {
                try {
                    uploadFileUtil.destroyFileWithUrl(mediaToDelete.getUrl());
                    product.getMedias().remove(mediaToDelete);
                    mediaRepository.delete(mediaToDelete);
                } catch (Exception e) {
                    log.warn("Failed to delete media with ID {} from cloud storage: {}",
                            mediaToDelete.getId(), e.getMessage(), e);
                }
            }
        }

        if (images != null && images.length > 0) {
            List<MultipartFile> imageList = List.of(images);
            List<String> newImageUrls = uploadFileUtil.uploadMultipleFiles(imageList);
            Date now = new Date();

            for (String imageUrl : newImageUrls) {
                Media media = Media.builder()
                        .url(imageUrl)
                        .type(MediaType.Image)
                        .product(product)
                        .build();
                media.setCreatedAt(now);
                media.setUpdatedAt(now);

                if (product.getMedias() == null) {
                    product.setMedias(new HashSet<>());
                }
                product.getMedias().add(media);
            }
        }

        product.setUpdatedAt(new Date());

        Product updatedProduct = productRepository.save(product);

        return productMapper.productToProductResponse(updatedProduct);
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
                .map(productMapper::productToProductResponse)
                .toList();

        return PaginationUtil.createPaginationResponse(productsPage, paginationRequest, productResponseList);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ProductResponseDto> filterProducts(PaginationRequestDto paginationRequest,
                                                                    String sortBy,
                                                                    String search) {
        log.info("Sorting by: {}; Search query: {}", sortBy, search);

        Page<Product> productsPage = getProductsPageByFilter(null, paginationRequest, sortBy, search);

        List<ProductResponseDto> productResponseDtoList = productsPage.getContent().stream()
                .map(productMapper::productToProductResponse)
                .toList();

        PaginationCustom paginationCustom = createPagination(paginationRequest, sortBy, productsPage);

        return PaginationResponseDto.<ProductResponseDto>builder()
                .pageCustom(paginationCustom)
                .items(productResponseDtoList)
                .build();
    }


    private Page<Product> getProductsPageByFilter(Long categoryId, // Tham số categoryId
                                                  PaginationRequestDto paginationRequest,
                                                  String sortBy,
                                                  String search) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        if (search != null && search.length() > 0) {
            String[] newSearch = StringUtils.split(search, "&");
            Pattern pattern = Pattern.compile(AppConstants.SEARCH_OPERATOR);
            for (String s : newSearch) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    searchCriteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<Product> products = getProducts(categoryId, paginationRequest, searchCriteriaList,  sortBy);
        Long totalElements = getTotalElements(categoryId, searchCriteriaList);

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize());

        return new PageImpl<>(products, pageable, totalElements);
    }


    private PaginationCustom createPagination(PaginationRequestDto paginationRequest,
                                              String sortBy,
                                              Page pages) {
        return PaginationCustom.builder()
                .pageNum(paginationRequest.getPageNum() + 1)
                .pageSize(pages.getSize())
                .totalElement(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .sortType(sortBy)
                .sortBy(determineSortByField(sortBy))
                .build();
    }

    private List<Product> getProducts(Long categoryId, PaginationRequestDto requestDto, List<SearchCriteria> searchCriteriaList, String sortBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Product> consumer = new SearchQueryCriteriaConsumer(predicate, cb, root);

        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        if (categoryId != null) {
            Join<Product, Category> categoryJoin = root.join("categories", JoinType.INNER);
            Predicate categoryPredicate = cb.equal(categoryJoin.get("id"), categoryId);
            predicate = cb.and(predicate, categoryPredicate);
        }

        if (searchCriteriaList.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("material"))) {
            List<String> materialValues = searchCriteriaList.stream()
                    .filter(c -> c.getKey().equalsIgnoreCase("material"))
                    .map(SearchCriteria::getValue)
                    .map(Object::toString)
                    .toList();
            if (materialValues.size() > 1) {
                predicate = cb.and(predicate, root.get("material").in(materialValues));
            }
        }

        query.where(predicate);

        if (sortBy != null) {
            if ("asc".equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.asc(root.get("price")));
                requestDto.setSortBy("price");
                requestDto.setSortType(sortBy);
            } else if ("desc".equalsIgnoreCase(sortBy)) {
                query.orderBy(cb.desc(root.get("price")));
                requestDto.setSortBy("price");
                requestDto.setSortType(sortBy);
            } else if ("discount_asc".equalsIgnoreCase(sortBy) || "discount_desc".equalsIgnoreCase(sortBy)) {
                Join<Product, Category> categoryJoin = root.join("categories", JoinType.LEFT);
                Join<Category, Promotion> promotionJoin = categoryJoin.join("promotion", JoinType.LEFT);

                if ("discount_asc".equalsIgnoreCase(sortBy)) {
                    query.orderBy(cb.asc(promotionJoin.get("discountPercent")));
                } else {
                    query.orderBy(cb.desc(promotionJoin.get("discountPercent")));
                }
                requestDto.setSortBy("discount");
                requestDto.setSortType(sortBy.substring(9));
            }
        }

        return entityManager.createQuery(query)
                .setFirstResult(requestDto.getPageNum() * requestDto.getPageSize())
                .setMaxResults(requestDto.getPageSize())
                .getResultList();
    }


    private Long getTotalElements(Long categoryId, List<SearchCriteria> searchCriteriaList) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> root = countQuery.from(Product.class);

        Join<Product, ProductVariation> variantsJoin = root.join("productVariations", JoinType.LEFT);

        Predicate predicate = cb.conjunction();
        SearchQueryCriteriaConsumer<Product> consumer = new SearchQueryCriteriaConsumer(predicate, cb, root);
        searchCriteriaList.forEach(consumer);
        predicate = consumer.getPredicate();

        if (categoryId != null) {
            Join<Product, Category> categoryJoin = root.join("categories", JoinType.INNER);
            Predicate categoryPredicate = cb.equal(categoryJoin.get("id"), categoryId);
            predicate = cb.and(predicate, categoryPredicate);
        }

        if (searchCriteriaList.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("color"))) {
            List<String> colorValues = searchCriteriaList.stream()
                    .filter(c -> c.getKey().equalsIgnoreCase("color"))
                    .map(SearchCriteria::getValue)
                    .map(Object::toString)
                    .toList();
        }

        if (searchCriteriaList.stream().anyMatch(c -> c.getKey().equalsIgnoreCase("material"))) {
            List<String> materialValues = searchCriteriaList.stream()
                    .filter(c -> c.getKey().equalsIgnoreCase("material"))
                    .map(SearchCriteria::getValue)
                    .map(Object::toString)
                    .toList();
            predicate = cb.and(predicate, root.get("material").in(materialValues));
        }

        countQuery.select(cb.countDistinct(root));
        countQuery.where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private String determineSortByField(String sortBy) {
        if (sortBy == null) {
            return null;
        } else if ("asc".equalsIgnoreCase(sortBy) || "desc".equalsIgnoreCase(sortBy)) {
            return "price";
        } else if ("discount_asc".equalsIgnoreCase(sortBy) || "discount_desc".equalsIgnoreCase(sortBy)) {
            return "discountPercent";
        }
        return null;
    }

}
