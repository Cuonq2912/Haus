package com.example.haus.domain.dto.response.statistic;

import com.example.haus.constant.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO cho thông tin đơn hàng")
public class RecentOrderResponseDto {
    @Schema(description = "ID của đơn hàng", example = "1")
    Long id;

    @Schema(description = "Ngày đặt hàng", example = "2023-10-15")
    LocalDate orderDate;

    @Schema(description = "Người đặt hàng", example = "Quân")
    String customerName;

    @Schema(description = "Trạng thái đơn hàng")
    OrderStatus status;

    @Schema(description = "Tổng tiền đơn hàng", example = "500000.0")
    Double totalAmount;
}
