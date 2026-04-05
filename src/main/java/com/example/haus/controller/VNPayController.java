package com.example.haus.controller;


import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.service.VNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "PAYMENT-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "payment-controller", description = "Payment APIs")
public class VNPayController {

    VNPayService vnPayService;

    @Operation(
            summary = "Lấy VNPay URL",
            description = "Tạo VNPay URL cho thanh toán VNPay với orderId",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Payment.GET_PAYMENT_URL)
    public ResponseEntity<ResponseData<String>> getVNPayUrl(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "orderId") Long orderId,
            HttpServletRequest request
    ) {
            String vnPayUrl = vnPayService.createVNPayUrl(orderId, userDetails.getUsername(), request);
            return ResponseUtil.success(
                            HttpStatus.OK,
                            SuccessMessage.Payment.GET_VNPAYURL_SUCCESS,
                    vnPayUrl);

    }

    @Operation(
        summary = "VNPay IPN (Instant Payment Notification)",
        description = "API này nhận thông báo thanh toán trực tiếp từ VNPay server (Trước vnpay return api) (Server-to-Server)"
    )
    @GetMapping(UrlConstant.Payment.VNPAY_IPN)
    public ResponseEntity<ResponseData<Map<String, String>>> vnPayIPN(@RequestParam Map<String, String> allParams) {

        Map<String, String> response = vnPayService.processVNPayIPN(allParams);
        return ResponseUtil.success(
                HttpStatus.OK,
                SuccessMessage.Payment.IPN_RECEIVED_SUCCESS,
                response);
    }
    

    @Operation(
            summary = "Xử lý VNPay return",
            description = "VNPay gọi về khi thanh toán xong (Callback URL)"
    )
    @GetMapping(UrlConstant.Payment.VNPAY_RETURN)
    public ResponseEntity<ResponseData<Object>> vnPayReturn(@RequestParam Map<String, String> allParams) {
        Map<String, Object> result = vnPayService.handleVNPayReturn(allParams);
        log.info("success = {}", result.get("success"));
        boolean success = (boolean) result.get("success");
        return success
                ? ResponseUtil.success(HttpStatus.OK, SuccessMessage.Payment.CALLBACK_VNPAY_SUCCESS, result)
                : ResponseUtil.success(HttpStatus.BAD_REQUEST, ErrorMessage.Payment.CALLBACK_VNPAY_FAIL, null);
    }

    

}
