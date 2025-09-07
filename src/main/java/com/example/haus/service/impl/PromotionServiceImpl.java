package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.request.promotion.PromotionRequestDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.mapper.PromotionMapper;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.PromotionRepository;
import com.example.haus.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionServiceImpl implements PromotionService {

    PromotionRepository promotionRepository;

    PromotionMapper promotionMapper;


    @Override
    public PromotionResponseDto addPromotion(PromotionRequestDto requestDto) {
        if (promotionRepository.existsByPromotionCode(requestDto.getPromotionCode())) {
            throw new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_EXISTED);
        }
        Promotion promotion = promotionMapper.promotionRequestDtoToPromotion(requestDto);
        return promotionMapper.promotionToPromotionResponseDto(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponseDto updatePromotion(Long id, PromotionRequestDto requestDto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Category.ERR_CATEGORY_NOT_EXISTED));
        promotionMapper.updatePromotionFromDto(requestDto, promotion);

        return promotionMapper.promotionToPromotionResponseDto(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponseDto getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));
        return promotionMapper.promotionToPromotionResponseDto(promotion);
    }

    @Override
    public List<PromotionResponseDto> getAllPromotion() {
        return promotionRepository.findAll()
                .stream().map(promotion ->
                        promotionMapper.promotionToPromotionResponseDto(promotion)).toList();
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));
        promotionRepository.delete(promotion);
    }

    @Override
    public PromotionResponseDto getPromotionByPromotionCode(String promotionCode) {
        Promotion promotion = promotionRepository.findByPromotionCode(promotionCode).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));
        return promotionMapper.promotionToPromotionResponseDto(promotion);
    }
}
