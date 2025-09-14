package com.example.haus.domain.dto.request.product;

import com.example.haus.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequestDto {

    @Schema(description = "Tên sản phẩm", example = "Ghế sofa phòng khách")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String productName;

    @Schema(description = "Mô tả sản phẩm", example = "Ghế sofa cao cấp, chất liệu da thật, thiết kế hiện đại")
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    String description;

}
