package com.example.haus.service;

import com.example.haus.domain.dto.request.product.momo.MomoIpnRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface MomoService {

    Map<String, String> createPaymentOrder(Long orderId, String username) throws JsonProcessingException;

    boolean handleIpnCallback(MomoIpnRequestDto request);

    Map<String, String> handleRedirectCallback(Map<String, String> params);

}
