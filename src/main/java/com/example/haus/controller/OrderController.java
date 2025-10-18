package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.service.OrderService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "INVOICE-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @Operation(
            summary = "Lấy chi tiết và xuất hóa đơn",
            description = "Truy vấn toàn bộ dữ liệu đơn hàng, sản phẩm, và người dùng để tạo hóa đơn."
    )
    @GetMapping("/api/v1/orders/{orderId}/invoice")
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
    @GetMapping("/api/v1/orders/{orderId}/invoice/pdf")
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
// --------------------------------------------------

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
}
