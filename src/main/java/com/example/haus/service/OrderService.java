package com.example.haus.service;

import com.example.haus.domain.dto.order.OrderAllRequestDto;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.dto.response.product.CreateOrderResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.itextpdf.text.DocumentException;
import java.io.IOException;

public interface OrderService {
    CreateOrderResponseDto createOrder(String username, OrderAllRequestDto orderAllRequestDto);

    InvoiceResponseDto getInvoiceDetails(Long orderId, String username);

    PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto paginationRequest, String status,
            String username);

    OrderResponseDto getOrderById(Long id, String username);

    OrderResponseDto getOrderByOrderNumber(String orderNumber, String username);

    OrderResponseDto updateStatusOrderById(Long id, String status);

    byte[] generateInvoicePdf(Long orderId, String username) throws DocumentException, IOException;

    void cancelOrder(String username, Long orderId);
}
