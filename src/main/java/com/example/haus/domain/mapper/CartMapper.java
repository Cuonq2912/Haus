package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.cart.CartItemResponseDto;
import com.example.haus.domain.dto.response.cart.CartResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {
        CartItemMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CartMapper {

    @Mapping(target = "cartItems", source = "products")
    CartResponseDto cartToCartResponse(CartItemResponseDto cartItemResponseDto);
}
