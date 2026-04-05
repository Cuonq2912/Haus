package com.example.haus.domain.mapper;

import com.example.haus.domain.dto.response.product.MediaResponseDto;
import com.example.haus.domain.entity.product.Media;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MediaMapper {

    MediaResponseDto mediaToMediaResponse(Media media);

}