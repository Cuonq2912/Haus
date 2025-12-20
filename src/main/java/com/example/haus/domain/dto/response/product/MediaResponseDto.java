package com.example.haus.domain.dto.response.product;

import com.example.haus.constant.MediaType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaResponseDto {

    Long id;

    String url;

    MediaType type;
}
