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
@Schema(description = "Response DTO cho thông tin sản phẩm trong đơn hàng (snapshot tại thời điểm đặt)")
public class OrderItemResponseDto {

    Long orderItemId;

    String productCode; // từ snapshotProductCode
    String productName; // từ snapshotProductName
    String color; // từ snapshotColor
    String size; // từ snapshotSize
    String material; // từ snapshotMaterial
    String imageUrl; // từ snapshotImageUrl

    Integer quantity;
    Double priceAtSale;
    Double total;

    Long originalProductVariationId;
}
