package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.request.promotion.PromotionRequestDto;
import com.example.haus.domain.dto.response.promotion.PromotionResponseDto;
import com.example.haus.domain.entity.product.Promotion;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PromotionMapper {

    Promotion promotionRequestDtoToPromotion (PromotionRequestDto requestDto);

    void updatePromotionFromDto(PromotionRequestDto requestDto, @MappingTarget Promotion promotion);

    @Mapping(target = "categoryId", source = "category.id")
    PromotionResponseDto promotionToPromotionResponseDto(Promotion promotion);

}
