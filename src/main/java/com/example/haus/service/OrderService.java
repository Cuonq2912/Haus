package com.example.haus.service;

import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

public interface OrderService {
    InvoiceResponseDto getInvoiceDetails(Long orderId);

    byte[] generateInvoicePdf(Long orderId) throws DocumentException, IOException;
}
