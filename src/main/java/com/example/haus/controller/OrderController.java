package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.order.OrderAllRequestDto;
import com.example.haus.domain.dto.order.OrderItemRequestDto;
import com.example.haus.domain.dto.order.OrderRequestDto;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.entity.product.OrderItem;
import com.example.haus.domain.dto.request.order.BuyNowRequest;
import com.example.haus.domain.dto.request.order.CheckoutRequest;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.service.OrderService;
import com.itextpdf.text.DocumentException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "ORDER-CONTROLLER")
@Tag(name = "order-controller", description = "Order APIs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @Operation(
            summary = "Tạo đơn hàng mới",
            description = "Giúp người dùng tạo đơn hàng mới.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping("" +
            "/orders")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody OrderAllRequestDto orderAllRequestDto) {
        String username = userDetails.getUsername();
        log.info("Username = {}", username);
        return ResponseUtil.success(
                SuccessMessage.Order.CREATE_ORDER_SUCCESS,
                orderService.createOrder(username, orderAllRequestDto)
        );
    }


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


    @Operation(
            summary = "Lấy chi tiết và xuất hóa đơn",
            description = "Truy vấn toàn bộ dữ liệu đơn hàng, sản phẩm, và người dùng để tạo hóa đơn."
    )
    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<?> getInvoiceDetails(@PathVariable Long orderId) {
        return ResponseUtil.success(
                SuccessMessage.Order.GET_INVOICE_SUCCESS,
                orderService.getInvoiceDetails(orderId)
        );
    }
    @Operation(
            summary = "Xuất hóa đơn PDF",
            description = "Truy vấn toàn bộ dữ liệu đơn hàng, sản phẩm, và người dùng để xuất hóa đơn."
    )
    @GetMapping("/orders/{orderId}/invoice/pdf")
    public ResponseEntity<?> exportPdf(@PathVariable Long orderId) {
        try {
            byte[] pdfBytes = orderService.generateInvoicePdf(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Thiết lập tên file sẽ tải xuống
            String filename = "invoice-" + orderId + ".pdf";

            // --- Bổ sung xử lý mã hóa UTF-8 cho tên file ---
            // Mã hóa tên tệp cho phần filename* (cho các ký tự non-ASCII)
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            // Thiết lập tiêu đề Content-Disposition: attachment
            headers.add(
                    "Content-Disposition",
                    "attachment; " +
                            "filename=\"" + filename + "\"; " + // Dùng tên file thường cho trình duyệt cũ
                            "filename*=UTF-8''" + encodedFilename // Dùng tên file mã hóa UTF-8 cho trình duyệt hiện đại
            );

            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        }
        catch (ResourceNotFoundException e) {
            // Xử lý lỗi không tìm thấy đơn hàng
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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


    @Operation(
            summary = "Cập nhật trạng thái đơn hàng theo ID(Admin)",
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

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return authentication.getName();
    }
}
