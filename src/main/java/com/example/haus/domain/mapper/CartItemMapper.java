package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.cart.ProductInCartResponseDto;
import com.example.haus.domain.entity.product.CartItem;
import com.example.haus.domain.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {ProductMapper.class, ProductVariationMapper.class}
)
public interface CartItemMapper {

    //service handle thù công
    default List<ProductInCartResponseDto> groupCartItemByProduct(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return List.of();
        }

        Map<Product, List<CartItem>> groupedByProduct = cartItems.stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getProductVariation().getProduct()));

        return groupedByProduct.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<CartItem> items = entry.getValue();

                    ProductInCartResponseDto productInCartResponseDto = ProductMapper.INSTANCE.toProductInCartResponseDto(product);

                    productInCartResponseDto.setProductVariations(
                            ProductVariationMapper.INSTANCE.cartItemListToProductVariationInCartDtoList(items)
                    );
                    return productInCartResponseDto;
                }).toList();
    }

}
