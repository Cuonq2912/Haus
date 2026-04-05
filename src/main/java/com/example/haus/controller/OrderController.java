package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.order.OrderAllRequestDto;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.dto.response.product.CreateOrderResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "ORDER-CONTROLLER")
@Tag(name = "order-controller", description = "Order APIs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @Operation(summary = "Tạo đơn hàng mới", description = "Giúp người dùng tạo đơn hàng mới.", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/orders")
    public ResponseEntity<ResponseData<CreateOrderResponseDto>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderAllRequestDto orderAllRequestDto) {
        String username = userDetails.getUsername();
        log.info("Username = {}", username);
        return ResponseUtil.success(SuccessMessage.Order.CREATE_ORDER_SUCCESS,
                orderService.createOrder(username, orderAllRequestDto));
    }

    @Operation(summary = "Lấy tất đơn hàng", description = "Lấy danh sách tất cả đơn hàng với phân trang và filter theo trạng thái", parameters = {
            @Parameter(name = "status", description = "Order status để filter (optional)", schema = @Schema(allowableValues = {
                    "pending", "confirmed", "processing", "delivered", "completed", "returned", "cancelled",
                    "refunded", }), example = "pending") }, security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(UrlConstant.Order.GET_ALL_ORDERS)
    public ResponseEntity<ResponseData<PaginationResponseDto<OrderResponseDto>>> getAllOrders(
            @AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(required = false) String status) {

        PaginationRequestDto paginationRequest = new PaginationRequestDto(pageNum, pageSize);
        return ResponseUtil.success(SuccessMessage.Order.GET_ORDER_SUCCESS,
                orderService.getAllOrders(paginationRequest, status, userDetails.getUsername()));
    }

    @Operation(summary = "Lấy chi tiết và xuất hóa đơn", description = "Truy vấn toàn bộ dữ liệu đơn hàng, sản phẩm, và người dùng để tạo hóa đơn.", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<ResponseData<InvoiceResponseDto>> getInvoiceDetails(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        String username = userDetails.getUsername();
        return ResponseUtil.success(SuccessMessage.Order.GET_INVOICE_SUCCESS,
                orderService.getInvoiceDetails(orderId, username));
    }

    @Operation(summary = "Hủy đơn hàng", description = "Hủy đơn hàng.", security = @SecurityRequirement(name = "Bearer Token"))
    @PostMapping("/orders/cancel/{orderId}")
    public ResponseEntity<ResponseData<Void>> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        String username = userDetails.getUsername();
        orderService.cancelOrder(username, orderId);

        return ResponseUtil.success(HttpStatus.OK, SuccessMessage.Order.CANCEL_ORDER_SUCCESS);
    }

    @Operation(summary = "Xuất hóa đơn PDF", description = "Truy vấn toàn bộ dữ liệu đơn hàng, sản phẩm, và người dùng để xuất hóa đơn.", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping("/orders/{orderId}/invoice/pdf")
    public ResponseEntity<byte[]> exportPdf(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        try {
            String username = userDetails.getUsername();
            byte[] pdfBytes = orderService.generateInvoicePdf(orderId, username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Thiết lập tên file sẽ tải xuống
            String filename = "invoice-" + orderId + ".pdf";

            // --- Bổ sung xử lý mã hóa UTF-8 cho tên file ---
            // Mã hóa tên tệp cho phần filename* (cho các ký tự non-ASCII)
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replace("\\+",
                    "%20");

            // Thiết lập tiêu đề Content-Disposition: attachment
            headers.add("Content-Disposition", "attachment; " + "filename=\"" + filename + "\"; " + // Dùng tên file
                                                                                                    // thường cho trình
                                                                                                    // duyệt cũ
                    "filename*=UTF-8''" + encodedFilename // Dùng tên file mã hóa UTF-8 cho trình duyệt hiện đại
            );

            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Xử lý lỗi không tìm thấy đơn hàng
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Lấy đơn hàng theo ID", description = "Dùng để lấy đơn hàng theo id", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(UrlConstant.Order.GET_ORDER_BY_ID)
    public ResponseEntity<ResponseData<OrderResponseDto>> getOrderById(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String username = userDetails.getUsername();
        return ResponseUtil.success(SuccessMessage.Order.GET_ORDER_SUCCESS, orderService.getOrderById(id, username));
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng theo ID(Admin)", description = "Dùng để cập nhật trạng thái đơn hàng theo id", parameters = {
            @Parameter(name = "status", description = "Order status", schema = @Schema(allowableValues = { "pending",
                    "confirmed", "processing", "delivered", "completed", "returned", "cancelled",
                    "refunded" }), example = "pending") }, security = @SecurityRequirement(name = "Bearer Token"))
    @PatchMapping(UrlConstant.Order.UPDATE_STATUS_ORDER_BY_ID)
    public ResponseEntity<ResponseData<OrderResponseDto>> updateStatusOrderById(@PathVariable Long id,
            @RequestParam String status) {
        return ResponseUtil.success(SuccessMessage.Order.UPDATE_STATUS_ORDER_SUCCESS,
                orderService.updateStatusOrderById(id, status));
    }

    @Operation(summary = "Lấy đơn hàng theo order number", description = "Dùng để lấy đơn hàng theo order number", security = @SecurityRequirement(name = "Bearer Token"))
    @GetMapping(UrlConstant.Order.GET_ORDER_BY_ORDER_NUMBER)
    public ResponseEntity<ResponseData<OrderResponseDto>> getOrderByOrderNumber(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable("orderNumber") String orderNumber) {
        String username = userDetails.getUsername();
        return ResponseUtil.success(SuccessMessage.Order.GET_ORDER_SUCCESS,
                orderService.getOrderByOrderNumber(orderNumber, username));
    }
}
