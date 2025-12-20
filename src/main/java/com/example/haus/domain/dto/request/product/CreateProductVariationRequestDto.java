package com.example.haus.domain.dto.request.product;

import com.example.haus.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductVariationRequestDto {

    @Schema(description = "Màu sắc sản phẩm", example = "Đỏ")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String color;

    @Schema(description = "Kích thước sản phẩm", example = "L")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String size;

    @Schema(description = "Giá biến thể sản phẩm", example = "1600000.0")
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @DecimalMin(value = "0", inclusive = false, message = ErrorMessage.Product.ERR_PRICE_INVALID)
    Long price;

    @Schema(description = "Số lượng tồn kho của biến thể", example = "20")
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    @Min(value = 0, message = ErrorMessage.Product.ERR_QUANTITY_INVALID)
    Integer inventoryQuantity;

    @Schema(description = "ID sản phẩm chính", example = "1")
    @NotNull(message = ErrorMessage.Product.ERR_PRODUCT_NOT_EXISTED)
    Long productId;
}
