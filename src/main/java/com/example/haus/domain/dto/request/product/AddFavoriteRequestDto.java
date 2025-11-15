package com.example.haus.domain.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request DTO để thêm sản phẩm vào danh sách yêu thích")
public class AddFavoriteRequestDto {

    @NotNull(message = "Product ID không được để trống")
    @Schema(description = "ID của sản phẩm cần thêm vào yêu thích", example = "123")
    Long productId;
}
