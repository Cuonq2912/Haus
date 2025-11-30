package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
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
import org.springframework.web.bind.annotation.RequestParam;

@RestApiV1
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {
    StatisticsService statisticsService;

    @Operation(
            summary = "Xuất hóa đơn theo quý (theo status) và Xuất Sale graph",
            description = "Xuất hóa đơn theo quý (theo status).",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping("/api/v1/statistics/order-by-month")
    public ResponseEntity<?> getOrderByMonth() {
        return ResponseUtil.success(
                SuccessMessage.Statistic.GET_STATISTIC_SUCCESS,
                statisticsService.getOrderByMonth()
        );
    }

    @Operation(
            summary = "Thống kê các đơn hàng gần đây",
            description = "Thống kê các đơn hàng gần đây.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping("/api/v1/statistics/get-recent-order")
    public ResponseEntity<?> getRecentOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize, "orderDate", "desc");
        return ResponseUtil.success(
                SuccessMessage.Order.GET_ORDER_SUCCESS,
                statisticsService.getRecentOrders(paginationRequest));
    }

    @Operation(
            summary = "Thống kê các sản phẩm bán chạy gần đây",
            description = "Thống kê các sản phẩm bán chạy gần đây.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping("/api/v1/statistics/get-best-seller")
    public ResponseEntity<?> getBestSellers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize, "orderDate", "desc");
        return ResponseUtil.success(
                SuccessMessage.Statistic.GET_STATISTIC_SUCCESS,
                statisticsService.getBestSellers(paginationRequest)
        );
    }
}
