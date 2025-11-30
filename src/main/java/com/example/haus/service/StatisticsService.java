package com.example.haus.service;

import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;

public interface StatisticsService {
    InvoiceResponseDto getStatistics(Long orderId);
}
