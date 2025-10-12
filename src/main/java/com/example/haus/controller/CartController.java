package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.request.auth.LoginRequestDto;
import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @Operation(
            summary = "Thêm 1 item vào giỏ hàng",
            description = "Dùng để user thêm 1 item vào giỏ hàng"
    )
    @PostMapping(UrlConstant.Cart.ADD_CART)
    public ResponseEntity<?> addItemToCart(@PathVariable String userId, @Valid @RequestBody CartRequest cartRequest) {
        return ResponseUtil.success(
                SuccessMessage.Cart.ADD_CART_SUCCESS,
                cartService.addToCart(userId, cartRequest)
        );
    }
}
