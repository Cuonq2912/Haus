package com.example.haus.domain.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariationResponseDto {

    Long id;

    String color;

    String size;

    Double price;

    Integer inventoryQuantity;

    Integer soldQuantity;

    Date createdAt;

    Date updatedAt;

    MediaResponseDto media;

}
