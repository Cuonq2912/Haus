package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.order.BuyNowRequest;
import com.example.haus.domain.dto.request.order.CheckoutRequest;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

public interface OrderService {
    InvoiceResponseDto getInvoiceDetails(Long orderId);

    PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto paginationRequest, String status);

    OrderResponseDto getOrderById(Long id);

    OrderResponseDto updateStatusOrderById(Long id, String status);

    byte[] generateInvoicePdf(Long orderId) throws DocumentException, IOException;

    OrderResponseDto checkoutFromCart(String username, CheckoutRequest request);

    OrderResponseDto buyNow(String username, BuyNowRequest request);
}
