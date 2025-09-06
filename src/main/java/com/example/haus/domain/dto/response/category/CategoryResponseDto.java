package com.example.haus.domain.dto.response.category;

import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDto {
    Long categoryId;
    String categoryName;
    String description;

    PromotionResponseDto promotion;
}

