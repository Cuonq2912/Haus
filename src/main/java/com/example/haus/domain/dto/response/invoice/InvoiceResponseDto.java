package com.example.haus.domain.dto.response.invoice;

// InvoiceResponseDto.java

import com.example.haus.constant.promotion.PromotionType;
import com.example.haus.domain.dto.response.order.OrderResponseDto;
import com.example.haus.domain.dto.response.payment.PaymentResponseDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import com.example.haus.domain.entity.user.Gender;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class InvoiceResponseDto {

    // --- 1. THÔNG TIN ĐƠN HÀNG (Từ Order) ---
    private OrderResponseDto responseDto;

    // --- 2. THÔNG TIN KHÁCH HÀNG (Từ Order -> User) ---
    private UserResponseDto user;

    // --- 3. THÔNG TIN THANH TOÁN (Từ Order -> Payment) ---
    private PaymentResponseDto payment;

    // --- 4. THÔNG TIN BIẾN THỂ VÀ GIÁ (Từ OrderItem và Product/ProductVariation) ---
    private List<InvoiceItemDto> items;

    // --- 5. THÔNG TIN KHUYẾN MÃI (Từ Order -> Promotion) ---
    private PromotionResponseDto promotion;

}