package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.cart.CartItemResponseDto;
import com.example.haus.domain.dto.response.cart.ProductInCartResponseDto;
import com.example.haus.domain.entity.product.Product;
import com.example.haus.domain.dto.request.product.ProductRequestDto;
import com.example.haus.domain.dto.request.product.UpdateProductRequestDto;
import com.example.haus.domain.dto.response.product.ProductResponseDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class, MediaMapper.class, ProductVariationMapper.class}
)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "categoriesName",
            expression = "java(product.getCategories().stream().map(com.example.haus.domain.entity.product.Category::getCategoryName).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "discountPercent",
            expression = "java(product.getCategories().stream()" +
                    "    .map(com.example.haus.domain.entity.product.Category::getPromotion)" +
                    "    .filter(java.util.Objects::nonNull)" +
                    "    .filter(p -> com.example.haus.constant.promotion.PromotionStatus.ACTIVE.equals(p.getStatus()))" +
                    "    .map(com.example.haus.domain.entity.product.Promotion::getDiscountPercent)" +
                    "    .filter(java.util.Objects::nonNull)" +
                    "    .max(java.util.Comparator.naturalOrder())" +
                    "    .orElse(null))")
    @Mapping(target = "daysRemaining",
            expression = "java(product.getCategories().stream()" +
                    "    .map(com.example.haus.domain.entity.product.Category::getPromotion)" +
                    "    .filter(java.util.Objects::nonNull)" +
                    "    .filter(p -> p.getEndDate() != null && p.getEndDate().isAfter(java.time.LocalDate.now()))" +
                    "    .max(java.util.Comparator.comparing(com.example.haus.domain.entity.product.Promotion::getDiscountPercent))" +
                    "    .map(com.example.haus.domain.entity.product.Promotion::getEndDate)" +
                    "    .map(ld -> java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), ld))" +
                    "    .orElse(0L))")
    ProductResponseDto productToProductResponse(Product product);

    @Mapping(target = "categories", ignore = true)
    Product createProductRequestDtoToProduct(ProductRequestDto request);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateProductFromUpdateDto(UpdateProductRequestDto request, @MappingTarget Product product);

    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productVariations", ignore = true)
    //CartItemResponse === ProductInCart
    ProductInCartResponseDto toProductInCartResponseDto(Product product);
}
