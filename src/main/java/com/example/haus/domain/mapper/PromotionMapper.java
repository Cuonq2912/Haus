package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.request.promotion.PromotionRequestDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.entity.product.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PromotionMapper {

    Promotion promotionRequestDtoToPromotion (PromotionRequestDto requestDto);

    void updatePromotionFromDto(PromotionRequestDto requestDto, @MappingTarget Promotion promotion);

    PromotionResponseDto promotionToPromotionResponseDto(Promotion promotion);

}
