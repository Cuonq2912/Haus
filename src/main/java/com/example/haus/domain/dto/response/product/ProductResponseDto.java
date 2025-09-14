package com.example.haus.domain.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDto {

    Long id;
    String productName;
    Double price;
    String description;
    Integer inventoryQuantity;
    List<String> color;
    List<String> imageUrl;

}
