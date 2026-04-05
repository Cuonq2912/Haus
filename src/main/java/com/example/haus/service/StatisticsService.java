package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.product.ProductStatisticResponseDto;
import com.example.haus.domain.dto.response.statistic.RecentOrderResponseDto;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getOrderByMonth();

    Map<String, Object> getSales(LocalDate startDate, LocalDate endDate);

    PaginationResponseDto<RecentOrderResponseDto> getRecentOrders(PaginationRequestDto paginationRequest,
            LocalDate startDate, LocalDate endDate);

    PaginationResponseDto<ProductStatisticResponseDto> getBestSellers(PaginationRequestDto paginationRequest,
            LocalDate startDate, LocalDate endDate);

    Map<String, Double> getSaleByCategories(LocalDate startDate, LocalDate endDate);

}
