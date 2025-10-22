package com.example.haus.domain.dto.response.product;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDto {

    Long id;

    String productCode;

    String productName;

    Double price;

    String description;

    String detailDescription;

    Integer inventoryQuantity;

    Integer soldQuantity;

    Date createdAt;

    Float discountPercent;

    Date updatedAt;

    Boolean isDeleted;

    List<MediaResponseDto> medias;

    List<String> categoriesName = new ArrayList<>();

    List<ProductVariationResponseDto> productVariations = new ArrayList<>();
}
