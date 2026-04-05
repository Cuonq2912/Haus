package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.ReviewRequestDto;
import com.example.haus.domain.dto.response.dashboard.TopReviewDto;
import com.example.haus.domain.dto.response.product.RatingStatisticsDto;
import com.example.haus.domain.dto.response.product.ReviewResponseDto;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.entity.product.Review;
import com.example.haus.domain.entity.user.User;
import com.example.haus.exception.ForBiddenException;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderItemRepository;
import com.example.haus.repository.ProductRepository;
import com.example.haus.repository.ReviewRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.ReviewService;
import com.example.haus.util.PaginationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.haus.constant.CommonConstant.CREATED_AT;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "REVIEW-SERVICE")
public class ReviewServiceImpl implements ReviewService {

    ReviewRepository reviewRepository;
    UserRepository userRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public ReviewResponseDto createReview(String userId, Long productId, ReviewRequestDto request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED);
        }

        List<com.example.haus.domain.entity.product.OrderItem> orderItems = orderItemRepository
                .findByUserIdAndProductIdAndOrderStatus(userId, productId, OrderStatus.COMPLETED);

        if (orderItems.isEmpty()) {
            throw new InvalidDataException(ErrorMessage.Review.ERR_REVIEW_CAN_NOT_BEFORE_BUY);
        }

        com.example.haus.domain.entity.product.OrderItem orderItem = orderItems.get(0);

        if (orderItem == null || orderItem.getId() == null) {
            throw new InvalidDataException(ErrorMessage.Review.ERR_REVIEW_CAN_NOT_BEFORE_BUY);
        }

        Review review = new Review();
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setUser(user);
        review.setProduct(product);
        review.setOrderItem(orderItem);
        review.setIsHidden(false);

        log.info("Review before save - orderItem: {}, orderItemId: {}", review.getOrderItem(),
                review.getOrderItem() != null ? review.getOrderItem().getId() : "NULL");

        Review savedReview = reviewRepository.save(review);
        log.info("savedReviewId: {}", savedReview.getId());

        return mapToResponseDto(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(String userId, Long reviewId, ReviewRequestDto request) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Review.ERR_REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForBiddenException(ErrorMessage.Review.ERR_REVIEW_UNAUTHORIZED_UPDATE);
        }

        review.setRating(request.getRating());
        review.setContent(request.getContent());

        Review updatedReview = reviewRepository.save(review);

        return mapToResponseDto(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(String userId, Long reviewId) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Review.ERR_REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForBiddenException(ErrorMessage.Review.ERR_REVIEW_UNAUTHORIZED_DELETE);
        }

        reviewRepository.deleteById(reviewId);
        reviewRepository.save(review);

    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Review.ERR_REVIEW_NOT_FOUND));

        return mapToResponseDto(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ReviewResponseDto> getProductReviews(Long productId,
            PaginationRequestDto paginationRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED);
        }

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, CREATED_AT));

        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);

        List<ReviewResponseDto> reviews = reviewPage.getContent().stream().map(this::mapToResponseDto).toList();

        return PaginationUtil.createPaginationResponse(reviewPage, paginationRequest, reviews);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ReviewResponseDto> getProductReviewsByRating(Long productId, Integer rating,
            PaginationRequestDto paginationRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED);
        }

        if (rating < 1 || rating > 5) {
            throw new InvalidDataException(ErrorMessage.Review.ERR_REVIEW_RATING_INVALID);
        }

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, CREATED_AT));

        Page<Review> reviewPage = reviewRepository.findByProductIdAndRating(productId, rating, pageable);

        List<ReviewResponseDto> reviews = reviewPage.getContent().stream().map(this::mapToResponseDto).toList();

        return PaginationUtil.createPaginationResponse(reviewPage, paginationRequest, reviews);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingStatisticsDto getProductRatingStatistics(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED));

        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED);
        }

        List<Review> reviews = reviewRepository.findByProductId(productId, Pageable.unpaged()).getContent().stream()
                .filter(r -> !r.getIsHidden()).toList();

        long totalReviews = reviews.size();

        if (totalReviews == 0) {
            return RatingStatisticsDto.builder().productId(productId).productName(product.getProductName())
                    .totalReviews(0L).averageRating(0.0).rating5Count(0L).rating4Count(0L).rating3Count(0L)
                    .rating2Count(0L).rating1Count(0L).rating5Percentage(0.0).rating4Percentage(0.0)
                    .rating3Percentage(0.0).rating2Percentage(0.0).rating1Percentage(0.0).build();
        }

        long rating5Count = reviews.stream().filter(r -> r.getRating() == 5).count();
        long rating4Count = reviews.stream().filter(r -> r.getRating() == 4).count();
        long rating3Count = reviews.stream().filter(r -> r.getRating() == 3).count();
        long rating2Count = reviews.stream().filter(r -> r.getRating() == 2).count();
        long rating1Count = reviews.stream().filter(r -> r.getRating() == 1).count();

        double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        return RatingStatisticsDto.builder().productId(productId).productName(product.getProductName())
                .totalReviews(totalReviews).averageRating(Math.round(averageRating * 10.0) / 10.0)
                .rating5Count(rating5Count).rating4Count(rating4Count).rating3Count(rating3Count)
                .rating2Count(rating2Count).rating1Count(rating1Count)
                .rating5Percentage(Math.round((rating5Count * 100.0 / totalReviews) * 10.0) / 10.0)
                .rating4Percentage(Math.round((rating4Count * 100.0 / totalReviews) * 10.0) / 10.0)
                .rating3Percentage(Math.round((rating3Count * 100.0 / totalReviews) * 10.0) / 10.0)
                .rating2Percentage(Math.round((rating2Count * 100.0 / totalReviews) * 10.0) / 10.0)
                .rating1Percentage(Math.round((rating1Count * 100.0 / totalReviews) * 10.0) / 10.0).build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<ReviewResponseDto> getMyReviews(String userId,
            PaginationRequestDto paginationRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Review> reviewPage = reviewRepository.findByUserId(userId, pageable);

        List<ReviewResponseDto> reviews = reviewPage.getContent().stream().map(this::mapToResponseDto).toList();

        return PaginationUtil.createPaginationResponse(reviewPage, paginationRequest, reviews);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<TopReviewDto> getTopReviews(PaginationRequestDto paginationRequest) {
        Pageable pageable = PageRequest.of(paginationRequest.getPageNum(), paginationRequest.getPageSize(),
                Sort.by(Sort.Direction.DESC, "rating", "createdAt"));

        Page<Review> reviewPage = reviewRepository.findTopReviewsByRating(pageable);

        List<TopReviewDto> reviews = reviewPage.getContent().stream().map(this::mapToTopReviewDto).toList();

        return PaginationUtil.createPaginationResponse(reviewPage, paginationRequest, reviews);
    }

    private ReviewResponseDto mapToResponseDto(Review review) {
        return ReviewResponseDto.builder().id(review.getId()).rating(review.getRating()).content(review.getContent())
                .productId(review.getProduct().getId()).productName(review.getProduct().getProductName())
                .orderItemId(review.getOrderItem().getId())
                .user(ReviewResponseDto.UserInfo.builder().userId(review.getUser().getId())
                        .username(review.getUser().getUsername()).firstName(review.getUser().getFirstName())
                        .lastName(review.getUser().getLastName()).build())
                .createdAt(review.getCreatedAt()).updatedAt(review.getUpdatedAt()).build();
    }

    private TopReviewDto mapToTopReviewDto(Review review) {
        String reviewerName = buildReviewerName(review.getUser());

        return TopReviewDto.builder().rating(review.getRating()).content(review.getContent()).reviewerName(reviewerName)
                .build();
    }

    private String buildReviewerName(User user) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        } else if (!firstName.isEmpty()) {
            return firstName;
        } else if (!lastName.isEmpty()) {
            return lastName;
        } else {
            return user.getUsername();
        }
    }

}
