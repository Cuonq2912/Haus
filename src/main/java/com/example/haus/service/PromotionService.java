package com.example.haus.service;

import com.example.haus.domain.dto.request.category.CategoryRequestDto;
import com.example.haus.domain.dto.request.promotion.PromotionRequestDto;
import com.example.haus.domain.dto.response.category.CategoryResponseDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;

import java.util.List;

public interface PromotionService {

    PromotionResponseDto addPromotion(PromotionRequestDto requestDto);

    PromotionResponseDto updatePromotion(Long id, PromotionRequestDto requestDto);

    PromotionResponseDto getPromotionById(Long id);

    List<PromotionResponseDto> getAllPromotion();

    void deletePromotion(Long id);

    PromotionResponseDto getPromotionByPromotionCode(String promotionCode);

}
