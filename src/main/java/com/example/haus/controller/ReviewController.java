package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.ReviewRequestDto;
import com.example.haus.domain.dto.response.dashboard.TopReviewDto;
import com.example.haus.domain.dto.response.product.RatingStatisticsDto;
import com.example.haus.domain.dto.response.product.ReviewResponseDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.security.CustomUserDetails;
import com.example.haus.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "REVIEW-CONTROLLER")
@Tag(name = "review-controller", description = "Product Review APIs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    @Operation(
            summary = "Tạo đánh giá sản phẩm",
            description = "Cho phép người dùng đánh giá sản phẩm đã mua. Chỉ có thể đánh giá sau khi đơn hàng hoàn thành.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping(UrlConstant.Review.CREATE_REVIEW)
    public ResponseEntity<ResponseData<ReviewResponseDto>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Parameter(description = "ID của sản phẩm cần đánh giá", example = "1") Long productId,
            @Valid @RequestBody ReviewRequestDto request) {
        
        String userId = userDetails.getUser().getId();
        return ResponseUtil.success(
                HttpStatus.CREATED,
                SuccessMessage.Review.CREATE_REVIEW_SUCCESS,
                reviewService.createReview(userId, productId, request)
        );
    }

    @Operation(
            summary = "Cập nhật đánh giá",
            description = "Cho phép người dùng cập nhật đánh giá của mình",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping(UrlConstant.Review.UPDATE_REVIEW)
    public ResponseEntity<ResponseData<ReviewResponseDto>> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Parameter(description = "ID của đánh giá cần cập nhật", example = "1") Long reviewId,
            @Valid @RequestBody ReviewRequestDto request) {
        
        String userId = userDetails.getUser().getId();
        return ResponseUtil.success(
                SuccessMessage.Review.UPDATE_REVIEW_SUCCESS,
                reviewService.updateReview(userId, reviewId, request)
        );
    }

    @Operation(
            summary = "Xóa đánh giá",
            description = "Cho phép người dùng xóa đánh giá của mình",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.Review.DELETE_REVIEW)
    public ResponseEntity<ResponseData<Void>> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Parameter(description = "ID của đánh giá cần xóa", example = "1") Long reviewId) {
        
        String userId = userDetails.getUser().getId();
        reviewService.deleteReview(userId, reviewId);
        return ResponseUtil.success(
                HttpStatus.OK,
                SuccessMessage.Review.DELETE_REVIEW_SUCCESS
        );
    }

    @Operation(
            summary = "Lấy đánh giá theo ID",
            description = "Lấy thông tin chi tiết của một đánh giá"
    )
    @GetMapping(UrlConstant.Review.GET_REVIEW_BY_ID)
    public ResponseEntity<ResponseData<ReviewResponseDto>> getReviewById(
            @PathVariable @Parameter(description = "ID của đánh giá", example = "1") Long reviewId) {
        
        return ResponseUtil.success(
                SuccessMessage.Review.GET_REVIEW_SUCCESS,
                reviewService.getReviewById(reviewId)
        );
    }

    @Tag(name = "public-review-controller", description = "Public Review APIs")
    @Operation(
            summary = "Lấy danh sách đánh giá của sản phẩm",
            description = "Lấy tất cả đánh giá của một sản phẩm với phân trang"
    )
    @GetMapping(UrlConstant.Review.GET_PRODUCT_REVIEWS)
    public ResponseEntity<ResponseData<PaginationResponseDto<ReviewResponseDto>>> getProductReviews(
            @PathVariable @Parameter(description = "ID của sản phẩm", example = "1") Long productId,
            @RequestParam(defaultValue = "1") @Parameter(description = "Số trang", example = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(description = "Số items trên mỗi trang", example = "10") Integer pageSize) {
        
        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
        return ResponseUtil.success(
                SuccessMessage.Review.GET_REVIEWS_SUCCESS,
                reviewService.getProductReviews(productId, paginationRequest)
        );
    }

    @Tag(name = "public-review-controller", description = "Public Review APIs")
    @Operation(
            summary = "Lấy đánh giá theo số sao",
            description = "Lọc đánh giá của sản phẩm theo số sao (1-5)"
    )
    @GetMapping(UrlConstant.Review.GET_PRODUCT_REVIEWS_BY_RATING)
    public ResponseEntity<ResponseData<PaginationResponseDto<ReviewResponseDto>>> getProductReviewsByRating(
            @PathVariable @Parameter(description = "ID của sản phẩm", example = "1") Long productId,
            @PathVariable @Parameter(description = "Số sao (1-5)", example = "5") Integer rating,
            @RequestParam(defaultValue = "1") @Parameter(description = "Số trang", example = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(description = "Số items trên mỗi trang", example = "10") Integer pageSize) {
        
        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
        return ResponseUtil.success(
                SuccessMessage.Review.GET_REVIEWS_SUCCESS,
                reviewService.getProductReviewsByRating(productId, rating, paginationRequest)
        );
    }

    @Tag(name = "public-review-controller", description = "Public Review APIs")
    @Operation(
            summary = "Lấy thống kê đánh giá sản phẩm",
            description = "Lấy thống kê số lượng và phần trăm đánh giá theo từng mức sao"
    )
    @GetMapping(UrlConstant.Review.GET_PRODUCT_RATING_STATISTICS)
    public ResponseEntity<ResponseData<RatingStatisticsDto>> getProductRatingStatistics(
            @PathVariable @Parameter(description = "ID của sản phẩm", example = "1") Long productId) {
        
        return ResponseUtil.success(
                SuccessMessage.Review.GET_RATING_STATISTICS_SUCCESS,
                reviewService.getProductRatingStatistics(productId)
        );
    }

    @Operation(
            summary = "Lấy danh sách đánh giá của tôi",
            description = "Lấy tất cả đánh giá mà người dùng đã tạo",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Review.GET_MY_REVIEWS)
    public ResponseEntity<ResponseData<PaginationResponseDto<ReviewResponseDto>>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") @Parameter(description = "Số trang", example = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Parameter(description = "Số items trên mỗi trang", example = "10") Integer pageSize) {
        
        String userId = userDetails.getUser().getId();
        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
        return ResponseUtil.success(
                SuccessMessage.Review.GET_MY_REVIEWS_SUCCESS,
                reviewService.getMyReviews(userId, paginationRequest)
        );
    }
    @Tag(name = "public-review-controller", description = "Public Review APIs")
    @Operation(
            summary = "Lấy danh sách đánh giá có số sao cao nhất",
            description = "Lấy danh sách đánh giá được sắp xếp theo số sao giảm dần. Không phân biệt sản phẩm."
    )
    @GetMapping(UrlConstant.Review.GET_TOP_REVIEWS)
    public ResponseEntity<ResponseData<PaginationResponseDto<TopReviewDto>>> getTopReviewsByRating(
            @RequestParam(defaultValue = "0") @Parameter(description = "Số trang", example = "1") Integer pageNum,
            @RequestParam(defaultValue = "15") @Parameter(description = "Số items trên mỗi trang", example = "15") Integer pageSize
    ) {
        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
        return ResponseUtil.success(
                SuccessMessage.Review.GET_TOP_REVIEWS_SUCCESS,
                reviewService.getTopReviews(paginationRequest)
        );
    }
}
