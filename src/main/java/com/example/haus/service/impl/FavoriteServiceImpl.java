package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.AddFavoriteRequestDto;
import com.example.haus.domain.dto.response.product.CheckFavoriteResponseDto;
import com.example.haus.domain.dto.response.product.FavoriteResponseDto;
import com.example.haus.domain.entity.product.Favorite;
import com.example.haus.domain.entity.product.Media;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.user.User;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.FavoriteRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    FavoriteRepository favoriteRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public FavoriteResponseDto addFavorite(String userId, AddFavoriteRequestDto request) {

        Long productId = request.getProductId();
        
        Product product = productRepository.findById(productId)
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED + ": " + productId));

        var existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isPresent()) {
            log.debug("Product {} already in favorites for user {}", productId, userId);
            return buildFavoriteResponse(existingFavorite.get());
        }

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

            Favorite favorite = Favorite.of(user, product);
            favorite = favoriteRepository.save(favorite);
            
            return buildFavoriteResponse(favorite);
            
        } catch (DataIntegrityViolationException e) {
            log.debug("Concurrent creation detected, fetching existing favorite");
            Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                    .orElseThrow(() -> new RuntimeException(ErrorMessage.Product.ERR_PRODUCT_FAVORITE_FAILED));
            return buildFavoriteResponse(favorite);
        }
    }


    @Override
    @Transactional
    public void removeFavorite(String userId, Long productId) {

        int deletedCount = favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        
        if (deletedCount > 0) {
            log.info("Successfully removed product {} from favorites for user {}", productId, userId);
        } else {
            log.debug("Product {} was not in favorites for user {}", productId, userId);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<FavoriteResponseDto> getFavorites(
            String userId, 
            PaginationRequestDto paginationRequest) {

        
        int pageIndex = paginationRequest.getPageNum();
        int pageSize = paginationRequest.getPageSize();
        
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        
        Page<FavoriteResponseDto> page = favoriteRepository
                .findByUserIdWithProductDetails(userId, pageable);
        
        PaginationCustom paginationCustom = PaginationCustom.builder()
                .pageNum(paginationRequest.getDisplayPageNum())
                .pageSize(paginationRequest.getPageSize())
                .totalElement(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
        
        return new PaginationResponseDto<>(paginationCustom, page.getContent());
    }


    @Override
    @Transactional(readOnly = true)
    public CheckFavoriteResponseDto checkFavorite(String userId, Long productId) {

        var favorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        
        return CheckFavoriteResponseDto.builder()
                .isFavorited(favorite.isPresent())
                .favoriteId(favorite.map(Favorite::getId).orElse(null))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long countFavorites(String userId) {
        long count = favoriteRepository.countByUserId(userId);
        log.debug("User {} has {} favorites", userId, count);
        return count;
    }


    private FavoriteResponseDto buildFavoriteResponse(Favorite favorite) {
        Product product = favorite.getProduct();
        
        String imageUrl = product.getMedias().stream()
                .findFirst()
                .map(Media::getUrl)
                .orElse(null);
        
        FavoriteResponseDto.ProductInfo productInfo = FavoriteResponseDto.ProductInfo.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .price(product.getPrice())
                .imageUrl(imageUrl)
                .build();
        
        return FavoriteResponseDto.builder()
                .id(favorite.getId())
                .product(productInfo)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
