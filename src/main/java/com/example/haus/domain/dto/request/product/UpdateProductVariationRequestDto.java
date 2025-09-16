package com.example.haus.domain.dto.request.product;

import com.example.haus.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductVariationRequestDto {

    @Schema(description = "ID của product variation cần cập nhật", example = "1")
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    Long id;

    @Schema(description = "File ảnh mới của biến thể sản phẩm")
    MultipartFile imageFile;

    @Schema(description = "Màu sắc mới của biến thể", example = "Xanh")
    @Size(max = 50, message = ErrorMessage.INVALID_SOME_THING_FIELD)
    String color;

    @Schema(description = "Kích thước mới của biến thể", example = "XL")
    @Size(max = 10, message = ErrorMessage.INVALID_SOME_THING_FIELD)
    String size;

    @Schema(description = "Giá mới của biến thể", example = "1700000.0")
    @DecimalMin(value = "0.0", inclusive = false, message = ErrorMessage.Product.ERR_PRICE_INVALID)
    Double price;

    @Schema(description = "Số lượng tồn kho mới của biến thể", example = "30")
    @Min(value = 0, message = ErrorMessage.Product.ERR_QUANTITY_INVALID)
    Integer inventoryQuantity;
}
