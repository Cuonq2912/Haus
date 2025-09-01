package com.example.haus.domain.request.product;

import com.example.haus.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequestDto {

    @Schema(description = "Tên sản phẩm cần cập nhật", example = "Ghế sofa phòng khách cao cấp")
    String productName;

    @Schema(description = "Giá sản phẩm mới", example = "1800000.0")
    @DecimalMin(value = "0.0", inclusive = false, message = ErrorMessage.Product.ERR_PRICE_INVALID)
    Double price;

    @Schema(description = "Mô tả sản phẩm mới", example = "Ghế sofa cao cấp, chất liệu da thật nhập khẩu, thiết kế hiện đại sang trọng")
    @Size(max = 5000, message = ErrorMessage.INVALID_SOME_THING_FIELD)
    String description;

    @Schema(description = "Số lượng tồn kho mới", example = "100")
    @Min(value = 0, message = ErrorMessage.Product.ERR_QUANTITY_INVALID)
    Integer inventoryQuantity;
}
