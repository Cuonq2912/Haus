package com.example.haus.service;

import com.example.haus.domain.dto.request.product.CodPaymentRequestDto;
import com.example.haus.domain.dto.response.product.CodPaymentResponseDto;

public interface CodPaymentService {

    CodPaymentResponseDto processCodPayment(CodPaymentRequestDto request, String username);
}
