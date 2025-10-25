package com.example.haus.service;

import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.domain.dto.response.cart.CartResponseDto;

public interface CartService {

    CartResponseDto addToCart(String email, CartRequest cartRequest);

    CartResponseDto getCart(String email);

    CartResponseDto removeItem(String email, Long productVariationId);

    void clearCart(String email);

    CartResponseDto updateQuantity(String email, CartRequest cartRequest);

}
