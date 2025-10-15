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
@Schema(description = "Request DTO cho thanh toán COD")
public class CodPaymentRequestDto {

    @NotNull(message = "Order ID is required")
    @Schema(description = "ID của đơn hàng cần thanh toán", example = "1")
    Long orderId;

    @Schema(description = "Địa chỉ giao hàng", example = "123 Đường ABC, Quận XYZ, TP.HCM")
    String shippingAddress;

    @Schema(description = "Số điện thoại liên hệ", example = "0901234567")
    String phoneNumber;

    @Schema(description = "Ghi chú thêm", example = "Giao hàng giờ hành chính")
    String note;
}
