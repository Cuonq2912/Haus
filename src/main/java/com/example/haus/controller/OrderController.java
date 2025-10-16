package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class OrderController {

        OrderService orderService;

        @Tag(name = "public-order-controller", description = "Public Order APIs")
        @Operation(
                summary = "Lấy tất đơn hàng",
                description = "Lấy danh sách tất cả đơn hàng với phân trang và filter theo trạng thái",
                parameters = {
                        @Parameter(name = "status", description = "Order status để filter (optional)",
                                schema = @Schema(allowableValues = {
                                        "pending", "confirmed", "processing", "delivered",
                                        "completed", "returned", "cancelled", "refunded",}), example = "pending")
                }, security = @SecurityRequirement(name = "Bearer Token")
        )
        @GetMapping(UrlConstant.Order.GET_ALL_ORDERS)
        public ResponseEntity<?> getAllOrders(
                        @RequestParam(defaultValue = "1") Integer pageNum,
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(required = false) String status) {

                PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
                return ResponseUtil.success(
                                SuccessMessage.Order.GET_ORDER_SUCCESS,
                                orderService.getAllOrders(paginationRequest, status));
        }

        @Tag(name = "admin-order-controller", description = "Admin Order APIs")
        @Operation(
                summary = "Lấy đơn hàng theo ID",
                description = "Dùng để lấy đơn hàng theo id",
                security = @SecurityRequirement(name = "Bearer Token")
        )
        @GetMapping(UrlConstant.Order.GET_ORDER_BY_ID)
        public ResponseEntity<?> getOrderById(
                        @PathVariable Long id) {
                return ResponseUtil.success(
                                SuccessMessage.Order.GET_ORDER_SUCCESS,
                                orderService.getOrderById(id));
        }

        @Tag(name = "admin-order-controller", description = "Admin Order APIs")
        @Operation(
                summary = "Cập nhật trạng thái đơn hàng theo ID",
                description = "Dùng để cập nhật trạng thái đơn hàng theo id",
                parameters = {
                        @Parameter(name = "status", description = "Order status", schema = @Schema(allowableValues = {
                                "pending", "confirmed", "processing", "delivered",
                                "completed", "returned", "cancelled", "refunded"}), example = "pending")
                },
                security = @SecurityRequirement(name = "Bearer Token")
        )
        @PostMapping(UrlConstant.Order.UPDATE_STATUS_ORDER_BY_ID)
        public ResponseEntity<?> updateStatusOrderById(
                        @PathVariable Long id,
                        @RequestParam String status) {
                return ResponseUtil.success(
                                SuccessMessage.Order.UPDATE_STATUS_ORDER_SUCCESS,
                                orderService.updateStatusOrderById(id, status));
        }

}
