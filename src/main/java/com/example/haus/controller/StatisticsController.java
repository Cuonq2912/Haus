package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestApiV1
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {
    StatisticsService statisticsService;

    @Operation(
            summary = "Lấy chi tiết và xuất hóa đơn",
            description = "Truy vấn toàn bộ dữ liệu đơn hàng, sản phẩm, và người dùng để tạo hóa đơn.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping("/api/v1/statistics/get-detail/{orderId}")
    public ResponseEntity<?> getInvoiceDetails(@PathVariable Long orderId) {
        return ResponseUtil.success(
                SuccessMessage.Order.GET_INVOICE_SUCCESS,
                statisticsService.getStatistics(orderId)
        );
    }
}
