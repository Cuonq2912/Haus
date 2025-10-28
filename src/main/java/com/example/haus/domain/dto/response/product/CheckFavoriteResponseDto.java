package com.example.haus.domain.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO kiểm tra sản phẩm đã được yêu thích chưa")
public class CheckFavoriteResponseDto {

    Boolean isFavorited;

    Long favoriteId;
}
