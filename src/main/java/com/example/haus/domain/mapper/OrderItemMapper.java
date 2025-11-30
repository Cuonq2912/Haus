package com.example.haus.domain.mapper;


import com.example.haus.domain.dto.order.OrderItemRequestDto;
import com.example.haus.domain.dto.response.product.OrderItemResponseDto;
import com.example.haus.domain.entity.product.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {MediaMapper.class}
)
public interface OrderItemMapper {


    OrderItem orderItemRequestDtoToOrderItem(OrderItemRequestDto orderItemRequestDto);

    @Mapping(target = "productId", source = "productVariation.product.id")
    @Mapping(target = "productCode", source = "productVariation.product.productCode")
    @Mapping(target = "productName", source = "productVariation.product.productName")
    @Mapping(target = "variationId", source = "productVariation.id")
    @Mapping(target = "color", source = "productVariation.color")
    @Mapping(target = "size", source = "productVariation.size")
    @Mapping(target = "image", source = "productVariation.media")
    @Mapping(target = "total", expression = "java(orderItem.getQuantity() * orderItem.getPriceAtSale())")
    OrderItemResponseDto orderItemToOrderItemResponseDto(OrderItem orderItem);
}
