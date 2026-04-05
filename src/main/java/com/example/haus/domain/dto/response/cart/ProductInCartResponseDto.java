package com.example.haus.domain.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInCartResponseDto {

    Long id;

    String productName;

    List<ProductVariationInCartResponseDto> productVariations;

}
