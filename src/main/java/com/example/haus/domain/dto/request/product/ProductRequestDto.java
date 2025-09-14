package com.example.haus.domain.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequestDto {

    @NotNull(message = "productName must be not null")
    String productName;

    @Min(value = 0, message = "price must be greater than or equal to 0")
    Double price;

    @NotNull(message = "description must be not null")
    String description;

    @Min(value = 0, message = "inventoryQuantity must be greater than or equal to 0")
    Integer inventoryQuantity;

    @NotNull(message = "color must be not null")
    List<String> color;

    @NotNull(message = "imageUrl must be not null")
    List<String> imageUrl;
}
