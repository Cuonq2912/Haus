package com.example.haus.service;

import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.domain.dto.response.cart.CartResponse;

public interface CartService {

    CartResponse addToCart(String email, CartRequest cartRequest);

    CartResponse getCart(String email);

    CartResponse removeItem(String email, Long productVariationId);

    void clearCart(String email);

    CartResponse updateQuantity(String email, CartRequest cartRequest);

}
