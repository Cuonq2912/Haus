package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.product.ProductStatisticResponseDto;
import com.example.haus.domain.dto.response.statistic.RecentOrderResponseDto;
import com.example.haus.domain.dto.response.statistic.RevenueDetailResponseDto;
import com.example.haus.domain.dto.response.statistic.StatisticResponseDto;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getOrderByMonth();

    PaginationResponseDto<RecentOrderResponseDto> getRecentOrders(PaginationRequestDto paginationRequest);

    PaginationResponseDto<ProductStatisticResponseDto> getBestSellers(PaginationRequestDto paginationRequest);

}
