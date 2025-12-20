package com.example.haus.domain.dto.response.cart;

import com.example.haus.domain.dto.response.product.MediaResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariationInCartResponseDto {

    Long id;

    String color;

    String size;

    Double price;

    Integer inventoryQuantity;

    Integer soldQuantity;

    @Builder.Default
    Integer cartQuantity = 0;

    Float discountPercent;

    MediaResponseDto media;

    Boolean isSelected = false;
}
