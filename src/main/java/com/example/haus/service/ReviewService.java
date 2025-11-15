package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.ReviewRequestDto;
import com.example.haus.domain.dto.response.product.RatingStatisticsDto;
import com.example.haus.domain.dto.response.product.ReviewResponseDto;

public interface ReviewService {
    
    ReviewResponseDto createReview(String userId, Long productId, ReviewRequestDto request);
    
    ReviewResponseDto updateReview(String userId, Long reviewId, ReviewRequestDto request);
    
    void deleteReview(String userId, Long reviewId);
    
    ReviewResponseDto getReviewById(Long reviewId);
    
    PaginationResponseDto<ReviewResponseDto> getProductReviews(Long productId, PaginationRequestDto paginationRequest);
    
    PaginationResponseDto<ReviewResponseDto> getProductReviewsByRating(Long productId, Integer rating, PaginationRequestDto paginationRequest);
    
    RatingStatisticsDto getProductRatingStatistics(Long productId);
    
    PaginationResponseDto<ReviewResponseDto> getMyReviews(String userId, PaginationRequestDto paginationRequest);
}
