package com.example.haus.domain.response.product;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDto {

    Long id;

    String productName;


    String description;

    Integer inventoryQuantity;

    Date updatedAt;

    Long categoryId;
}
