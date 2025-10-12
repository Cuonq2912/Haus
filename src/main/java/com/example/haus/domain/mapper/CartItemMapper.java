package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.cart.CartItemResponse;
import com.example.haus.domain.entity.product.CartItem;
import com.example.haus.domain.entity.product.ProductVariation;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CartItemMapper {

    @Mapping(target = "productVariationId", source = "productVariation.id")
    CartItemResponse cartItemToCartItemResponse(CartItem cartItem);

}
