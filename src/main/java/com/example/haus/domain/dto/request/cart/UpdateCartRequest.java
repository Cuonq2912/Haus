package com.example.haus.domain.dto.request.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Yêu cầu cập nhật sản phẩm vào giỏ hàng")
public record UpdateCartRequest(
        @Schema(description = "ID của biến thể sản phẩm", example = "1")
        @NotNull(message = "ID biến thể sản phẩm không được để trống")
        Long variantId,

        @Schema(description = "Số lượng sản phẩm muốn thêm vào giỏ hàng", example = "2", minimum = "1")
        @NotNull(message = "Số lượng sản phẩm không được để trống")
        @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
        @JsonProperty("quantity")
        Integer quantity,

        @Schema(description = "ID của biến thể sản phẩm muốn thay đổi", example = "2")
        @NotNull(message = "ID biến thể sản phẩm muốn thay đổi không được để trống")
        Long changedVariantId
) {
}