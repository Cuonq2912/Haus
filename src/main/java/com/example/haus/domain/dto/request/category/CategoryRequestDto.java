package com.example.haus.domain.dto.request.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequestDto {

//    @NotNull(message = "Category parent must be not null")
    Long parentId;

    @NotEmpty(message = "Category name is required")
    String categoryName;

    @NotNull(message = "Description must be not null")
    String description;
}
