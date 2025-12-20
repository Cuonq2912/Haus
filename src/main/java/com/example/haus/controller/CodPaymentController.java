package com.example.haus.controller;

import com.example.haus.base.ResponseUtil;
import com.example.haus.base.RestApiV1;
import com.example.haus.constant.SuccessMessage;
import com.example.haus.constant.UrlConstant;
import com.example.haus.domain.dto.request.product.CodPaymentRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.dto.response.product.CodPaymentResponseDto;
import com.example.haus.domain.dto.response.utils.ResponseData;
import com.example.haus.service.CodPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@RequiredArgsConstructor
@Slf4j(topic = "COD-PAYMENT-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "payment-controller", description = "Payment APIs")
public class CodPaymentController {

    CodPaymentService codPaymentService;

    @Operation(
            summary = "Thanh toán khi nhận hàng (COD)",
            description = "Xử lý thanh toán COD - Cash on Delivery. Khách hàng sẽ thanh toán khi nhận hàng.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping(UrlConstant.Payment.COD_PAYMENT)
    public ResponseEntity<ResponseData<CodPaymentResponseDto>> processCodPayment(@Valid @RequestBody CodPaymentRequestDto request) {

        CodPaymentResponseDto response = codPaymentService.processCodPayment(request);

        return ResponseUtil.success(
                HttpStatus.OK,
                SuccessMessage.Payment.COD_PAYMENT_SUCCESS,
                response
        );
    }
}
