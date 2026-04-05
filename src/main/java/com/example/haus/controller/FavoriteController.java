package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.AddFavoriteRequestDto;
import com.example.haus.domain.dto.response.product.CheckFavoriteResponseDto;
import com.example.haus.domain.dto.response.product.FavoriteResponseDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.security.CustomUserDetails;
import com.example.haus.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Tag(name = "favorite-controller", description = "Product Favorites Management APIs")
public class FavoriteController {

        FavoriteService favoriteService;

        @Operation(summary = "Thêm sản phẩm vào yêu thích", description = "Thêm sản phẩm vào danh sách yêu thích của user.", security = @SecurityRequirement(name = "Bearer Token"))
        @PostMapping(UrlConstant.Product.ADD_FAVORITE)
        public ResponseEntity<ResponseData<FavoriteResponseDto>> addFavorite(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        @Valid @RequestBody AddFavoriteRequestDto request) {

                String userId = userDetails.getUser().getId();
                var result = favoriteService.addFavorite(userId, request);

                return ResponseUtil.success(
                                HttpStatus.CREATED,
                                SuccessMessage.Product.ADD_FAVORITE_SUCCESS,
                                result);
        }

        @Operation(summary = "Xóa sản phẩm khỏi yêu thích", description = "Xóa sản phẩm khỏi danh sách yêu thích của user.", security = @SecurityRequirement(name = "Bearer Token"))
        @DeleteMapping(UrlConstant.Product.REMOVE_FAVORITE)
        public ResponseEntity<ResponseData<Void>> removeFavorite(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        @PathVariable @Parameter(description = "ID của sản phẩm cần xóa", example = "123") Long productId) {

                String userId = userDetails.getUser().getId();
                favoriteService.removeFavorite(userId, productId);

                return ResponseUtil.success(
                                HttpStatus.OK,
                                SuccessMessage.Product.REMOVE_FAVORITE_SUCCESS);
        }

        @Operation(summary = "Lấy danh sách sản phẩm yêu thích", description = "Lấy danh sách tất cả sản phẩm yêu thích của user với phân trang. "
                        +
                        "Sắp xếp theo thời gian thêm (mới nhất trước).", security = @SecurityRequirement(name = "Bearer Token"))
        @GetMapping(UrlConstant.Product.GET_FAVORITES)
        public ResponseEntity<ResponseData<PaginationResponseDto<ProductResponseDto>>> getFavorites(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam(defaultValue = "1") @Parameter(description = "Số trang (1..N)", example = "1") Integer pageNum,
                        @RequestParam(defaultValue = "20") @Parameter(description = "Số items trên mỗi trang", example = "20") Integer pageSize) {

                String userId = userDetails.getUser().getId();
                PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
                var result = favoriteService.getFavorites(userId, paginationRequest);

                return ResponseUtil.success(
                                SuccessMessage.Product.GET_FAVORITES_SUCCESS,
                                result);
        }

        @Operation(summary = "Kiểm tra sản phẩm đã yêu thích", description = "Kiểm tra xem sản phẩm có trong danh sách yêu thích của user hay không.", security = @SecurityRequirement(name = "Bearer Token"))
        @GetMapping(UrlConstant.Product.CHECK_FAVORITE)
        public ResponseEntity<ResponseData<CheckFavoriteResponseDto>> checkFavorite(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        @PathVariable @Parameter(description = "ID của sản phẩm", example = "123") Long productId) {

                String userId = userDetails.getUser().getId();
                var result = favoriteService.checkFavorite(userId, productId);

                return ResponseUtil.success(
                                SuccessMessage.Product.CHECK_FAVORITE_SUCCESS,
                                result);
        }
}
