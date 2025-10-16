package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;

public interface OrderService {

    PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto paginationRequest, String status);

    OrderResponseDto getOrderById(Long id);

    OrderResponseDto updateStatusOrderById(Long id, String status);
}
