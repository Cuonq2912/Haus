package com.example.haus.domain.dto.response.product;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDto {

    String productCode;

    String productName;

    String description;

    String detailDescription;

    Integer inventoryQuantity;

    Date updatedAt;

    Long categoryId;
}
