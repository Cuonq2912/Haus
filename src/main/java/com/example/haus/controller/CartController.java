package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.request.auth.LoginRequestDto;
import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.domain.dto.request.cart.UpdateCartRequest;
import com.example.haus.domain.dto.response.cart.CartResponseDto;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @Operation(
            summary = "Thêm 1 item vào giỏ hàng",
            description = "Dùng để user thêm 1 item vào giỏ hàng",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping(UrlConstant.Cart.ADD_CART)
    public ResponseEntity<ResponseData<CartResponseDto>> addItemToCart(@Valid @RequestBody CartRequest cartRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseUtil.success(
                SuccessMessage.Cart.ADD_CART_SUCCESS,
                cartService.addToCart(userDetails.getUsername(), cartRequest)
        );
    }

    @Operation(
            summary = "Lấy thông tin giỏ hàng",
            description = "Lấy thông tin giỏ hàng của người dùng đã xác thực.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Cart.GET_CART_BY_USER_ID)
    public ResponseEntity<ResponseData<CartResponseDto>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseUtil.success(
                SuccessMessage.Cart.GET_CART_SUCCESS,
                cartService.getCart(userDetails.getUsername())
        );
    }

    @Operation(
            summary = "Xóa sản phẩm khỏi giỏ hàng",
            description = "Xóa sản phẩm khỏi giỏ hàng dựa trên variantId.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.Cart.REMOVE_CART_ITEM_FROM_CART)
    public ResponseEntity<ResponseData<CartResponseDto>> removeCartItemFromCart(@PathVariable(name = "variantId") Long variantId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseUtil.success(
                SuccessMessage.Cart.DELETE_CART_ITEM_FROM_CART,
                cartService.removeItem(userDetails.getUsername(), variantId)
        );
    }

    @Operation(
            summary = "Cập nhật sản phẩm trong giỏ hàng",
            description = "Cập nhật của một sản phẩm trong giỏ hàng dựa trên newVariantId và oldVariantId.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PatchMapping(UrlConstant.Cart.UPDATE_CART)
    public ResponseEntity<ResponseData<CartResponseDto>> updateCart(@RequestBody @Valid UpdateCartRequest updateCartRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseUtil.success(
                SuccessMessage.Cart.UPDATE_CART_SUCCESS,
                cartService.updateCart(userDetails.getUsername(), updateCartRequest)
        );
    }

    @Operation(
            summary = "Xóa toàn bộ giỏ hàng",
            description = "Xóa tất cả các sản phẩm trong giỏ hàng của người dùng.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping(UrlConstant.Cart.DELETE_CART)
    public ResponseEntity<ResponseData<Void>> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseUtil.success(
                HttpStatus.NO_CONTENT,
                SuccessMessage.Cart.DELETE_CART_SUCCESS
        );
    }
}
