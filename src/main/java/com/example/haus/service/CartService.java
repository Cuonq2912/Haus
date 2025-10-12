package com.example.haus.service;

import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.domain.dto.response.cart.CartResponse;

public interface CartService {

    CartResponse addToCart(String userId, CartRequest cartRequest);

    CartResponse getCart(String userId);

    CartResponse removeItem(String userId, String productVariationId);

    void clearCart(String userId);

    CartResponse updateQuantity(String userId, CartRequest cartRequest);

}
