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
@Schema(description = "Response DTO for order creation")
public class CreateOrderResponseDto {

    @Schema(description = "ID của đơn hàng vừa tạo", example = "2")
    Long orderId;
}
