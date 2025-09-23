package com.example.haus.domain.dto.response.category;

import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString()
public class CategoryResponseDto {
    Long id;
    String categoryName;
    String description;

    Long parentId;

    List<CategoryResponseDto> subCategories = new ArrayList<>();

    PromotionResponseDto promotion;
}