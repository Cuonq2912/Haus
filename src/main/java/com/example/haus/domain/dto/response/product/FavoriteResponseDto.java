package com.example.haus.domain.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO cho thông tin sản phẩm yêu thích")
public class FavoriteResponseDto {

    Long id;

    ProductInfo product;

    Date createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Schema(description = "Thông tin cơ bản của sản phẩm")
    public static class ProductInfo {
        Long id;

        String productCode;

        String productName;

        Double price;

        String imageUrl;

    }

    public FavoriteResponseDto(Long id, Date createdAt, Long productId, String productCode, String productName,
            Double price, String imageUrl) {
        this.id = id;
        this.createdAt = createdAt;
        this.product = ProductInfo.builder().id(productId).productCode(productCode).productName(productName)
                .price(price).imageUrl(imageUrl).build();
    }
}
