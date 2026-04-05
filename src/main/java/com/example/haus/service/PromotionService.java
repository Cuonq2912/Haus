package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.promotion.PromotionRequestDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;

public interface PromotionService {

    PromotionResponseDto addPromotion(PromotionRequestDto requestDto);

    PromotionResponseDto updatePromotion(Long id, PromotionRequestDto requestDto);

    PromotionResponseDto getPromotionById(Long id);

    void deletePromotion(Long id);

    PromotionResponseDto getPromotionByPromotionCode(String promotionCode);

    PaginationResponseDto<PromotionResponseDto> filterPromotions(PaginationRequestDto paginationRequest,
            String sortByPrice, String... search);

}
