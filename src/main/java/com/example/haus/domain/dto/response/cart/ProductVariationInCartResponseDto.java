package com.example.haus.domain.dto.response.cart;

import com.example.haus.domain.dto.response.product.MediaResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;


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

    Float discountPercent;

    MediaResponseDto media;
}
