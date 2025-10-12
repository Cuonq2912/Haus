package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.domain.dto.response.cart.CartResponse;
import com.example.haus.domain.entity.product.Cart;
import com.example.haus.domain.entity.product.CartItem;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.mapper.CartItemMapper;
import com.example.haus.domain.mapper.CartMapper;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CartItemRepository;
import com.example.haus.repository.CartRepository;
import com.example.haus.repository.ProductVariationRepository;
import com.example.haus.service.CartService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j(topic = "CART-SERVICE")
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;

    ProductVariationRepository productVariationRepository;

    CartMapper cartMapper;

    CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public CartResponse addToCart(String userId, CartRequest cartRequest) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Cart.ERR_CART_NOT_FOUND));

        if (cartRequest.inventoryQuantity() <= 0) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_QUANTITY_INVALID);
        }

        ProductVariation productVariation = productVariationRepository.findByIdAndIsDeletedFalse(cartRequest.variantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));
        if(productVariation.getInventoryQuantity() < cartRequest.inventoryQuantity()) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_QUANTITY_INVALID);
        }

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariation().getId().equals(cartRequest.variantId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.inventoryQuantity());
        }
        else {
            CartItem newItem = CartItem.builder()
                    .quantity(cartRequest.inventoryQuantity())
                    .cart(cart)
                    .productVariation(productVariation)
                    .build();
            cart.getCartItems().add(newItem);
        }

        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.cartToCartResponse(updatedCart);
    }

    @Override
    public CartResponse getCart(String userId) {
        return null;
    }

    @Override
    public CartResponse removeItem(String userId, String productVariationId) {
        return null;
    }

    @Override
    public void clearCart(String userId) {

    }

    @Override
    public CartResponse updateQuantity(String userId, CartRequest cartRequest) {
        return null;
    }
}
