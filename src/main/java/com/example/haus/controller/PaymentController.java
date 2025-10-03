package com.example.haus.controller;


import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
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
public class PaymentController {

    VNPayService vnPayService;

    @Operation(
            summary = "Lấy VNPay URL",
            description = "Tạo VNPay URL cho thanh toán VNPay với orderId",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Payment.GET_PAYMENT_URL)
    public ResponseEntity<?> getVNPayUrl(
            @RequestParam(value = "orderId") Long orderId,
            HttpServletRequest request
    ) {
        String VNPayUrl = vnPayService.createVNPayUrl(orderId, request);
        return ResponseUtil.success(
                HttpStatus.OK,
                SuccessMessage.Payment.GET_VNPAYURL_SUCCESS,
                VNPayUrl);
    }

    @Operation(
            summary = "Xử lý VNPay return",
            description = "VNPay gọi về khi thanh toán xong",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @GetMapping(UrlConstant.Payment.VNPAY_RETURN)
    public ResponseEntity<?> vnPayReturn(@RequestParam Map<String, String> allParams) {
        boolean success = vnPayService.checkVNPayCallback(allParams);

        return success
                ? ResponseUtil.success(HttpStatus.OK, SuccessMessage.Payment.CALLBACK_VNPAY_SUCCESS)
                : ResponseUtil.error(HttpStatus.BAD_REQUEST, ErrorMessage.Payment.CALLBACK_VNPAY_FAIL);
    }

}
