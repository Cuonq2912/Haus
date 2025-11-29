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

    InvoiceResponseDto getInvoiceDetails(Long orderId);

    PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto paginationRequest, String status);

    OrderResponseDto getOrderById(Long id);

    OrderResponseDto getOrderByOrderNumber(String orderNumber);

    OrderResponseDto updateStatusOrderById(Long id, String status);

    byte[] generateInvoicePdf(Long orderId) throws DocumentException, IOException;

}
