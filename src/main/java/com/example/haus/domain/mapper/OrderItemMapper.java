package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.order.OrderItemRequestDto;
import com.example.haus.domain.dto.response.product.OrderItemResponseDto;
import com.example.haus.domain.entity.product.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = {
        MediaMapper.class })
public interface OrderItemMapper {

    OrderItem orderItemRequestDtoToOrderItem(OrderItemRequestDto orderItemRequestDto);

    @Mapping(target = "orderItemId", source = "id")
    @Mapping(target = "productCode", source = "snapshotProductCode")
    @Mapping(target = "productName", source = "snapshotProductName")
    @Mapping(target = "color", source = "snapshotColor")
    @Mapping(target = "size", source = "snapshotSize")
    @Mapping(target = "material", source = "snapshotMaterial")
    @Mapping(target = "imageUrl", source = "snapshotImageUrl")
    @Mapping(target = "originalProductVariationId", source = "productVariation.id")
    @Mapping(target = "total", expression = "java(orderItem.getQuantity() * orderItem.getPriceAtSale())")
    OrderItemResponseDto orderItemToOrderItemResponseDto(OrderItem orderItem);
}
