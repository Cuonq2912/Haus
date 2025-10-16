package com.example.haus.domain.dto.response.product;

import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response DTO cho thông tin đơn hàng")
public class OrderResponseDto {

    @Schema(description = "ID của đơn hàng", example = "1")
    Long id;

    @Schema(description = "Mã đơn hàng", example = "ORD-20231015-001")
    String orderNumber;

    @Schema(description = "Phí vận chuyển", example = "30000.0")
    Double shippingFee;

    @Schema(description = "Tổng tiền đơn hàng", example = "500000.0")
    Double totalAmount;

    @Schema(description = "Trạng thái đơn hàng")
    OrderStatus status;

    @Schema(description = "Ngày đặt hàng", example = "2023-10-15")
    LocalDate orderDate;

    @Schema(description = "Ngày giao hàng dự kiến", example = "2023-10-20")
    LocalDate deliveryDate;

    @Schema(description = "Thông tin khách hàng")
    UserInfo user;

    @Schema(description = "Thông tin khuyến mãi")
    PromotionInfo promotion;

    @Schema(description = "Thông tin thanh toán")
    PaymentInfo payment;

    @Schema(description = "Ngày tạo")
    Date createdAt;

    @Schema(description = "Ngày cập nhật cuối")
    Date updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserInfo {

        @Schema(description = "Username")
        String username;

        @Schema(description = "Email")
        String email;

        @Schema(description = "Họ")
        String firstName;

        @Schema(description = "Tên")
        String lastName;

        @Schema(description = "Số điện thoại")
        String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PromotionInfo {

        @Schema(description = "Mã khuyến mãi")
        String code;

        @Schema(description = "Phần trăm giảm giá")
        Integer discountPercent;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentInfo {

        @Schema(description = "Số tiền thanh toán")
        Double amount;

        @Schema(description = "Loại thanh toán")
        PaymentType type;

        @Schema(description = "Trạng thái thanh toán")
        PaymentStatus status;
    }
}
