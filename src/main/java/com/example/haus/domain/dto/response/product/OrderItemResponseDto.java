package com.example.haus.domain.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO cho thông tin sản phẩm trong đơn hàng")
public class OrderItemResponseDto {

    Long productId;

    String productCode;

    String productName;

    Long variationId;

    String color;

    String size;

    Integer quantity;

    Double priceAtSale;

    Double total;

    MediaResponseDto image;
}
