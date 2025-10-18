package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.request.cart.CartRequest;
import com.example.haus.domain.dto.response.cart.CartResponse;
import com.example.haus.domain.entity.product.Cart;
import com.example.haus.domain.entity.product.CartItem;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.CartItemMapper;
import com.example.haus.domain.mapper.CartMapper;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CartItemRepository;
import com.example.haus.repository.CartRepository;
import com.example.haus.repository.ProductVariationRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.service.CartService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j(topic = "CART-SERVICE")
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;

    ProductVariationRepository productVariationRepository;

    CartMapper cartMapper;

    CartItemRepository cartItemRepository;

    UserRepository userRepository;

    @Transactional
    @Override
    public CartResponse addToCart(String email, CartRequest cartRequest) {

        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Cart.ERR_CART_NOT_FOUND));

        if (cartRequest.quantity() <= 0) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_QUANTITY_INVALID);
        }

        ProductVariation productVariation = productVariationRepository.findByIdAndIsDeletedFalse(cartRequest.variantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariation().getId().equals(cartRequest.variantId()))
                .findFirst()
                .orElse(null);

        if(productVariation.getInventoryQuantity() < (cartRequest.quantity() + (cartItem != null ? cartItem.getQuantity() : 0))) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_QUANTITY_INVALID);
        }

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.quantity());
        }
        else {
            CartItem newItem = CartItem.builder()
                    .quantity(cartRequest.quantity())
                    .cart(cart)
                    .productVariation(productVariation)
                    .build();
            cart.getCartItems().add(newItem);
        }

        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.cartToCartResponse(updatedCart);
    }

    @Override
    public CartResponse getCart(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Cart.ERR_CART_NOT_FOUND));


        return cartMapper.cartToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(String email, Long productVariationId) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Cart.ERR_CART_NOT_FOUND));


        boolean removed = cart.getCartItems().removeIf(item -> item.getProductVariation().getId().equals(productVariationId));

        if (!removed) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_ITEM_NOT_EXISTED_IN_CART);
        }

        return cartMapper.cartToCartResponse(cartRepository.save(cart));
    }

    @Override
    public void clearCart(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Cart.ERR_CART_NOT_FOUND));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(String email, CartRequest cartRequest) {
        if (cartRequest.quantity() <= 0) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_QUANTITY_INVALID);
        }

        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Cart.ERR_CART_NOT_FOUND));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariation().getId().equals(cartRequest.variantId()))
                .findFirst()
                .orElse(null);

        if (existingItem == null) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_ITEM_NOT_EXISTED_IN_CART);
        }

        ProductVariation productVariation = productVariationRepository.findByIdAndIsDeletedFalse(cartRequest.variantId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));

        if(productVariation.getInventoryQuantity() < cartRequest.quantity() ) {
            throw new InvalidDataException(ErrorMessage.Cart.ERR_CART_QUANTITY_INVALID);
        }

        existingItem.setQuantity(cartRequest.quantity());

        cartItemRepository.save(existingItem);

        return cartMapper.cartToCartResponse(cart);
    }
}
