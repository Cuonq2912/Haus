package com.example.haus.domain.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStatisticResponseDto {
    Long id;
    String image;
    String productName;
    Double price;
    Float discountPercent;
    Integer soldQuantity;
}
