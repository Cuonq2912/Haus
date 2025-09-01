package com.example.haus.domain.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariationResponseDto {

    String imageUrl;

    String color;

    String size;

    Double price;

    Integer inventoryQuantity;
}
