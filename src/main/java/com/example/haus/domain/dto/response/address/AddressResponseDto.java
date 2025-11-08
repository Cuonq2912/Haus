package com.example.haus.domain.dto.response.address;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponseDto {

    Long id;

    String recipientName;

    String phoneNumber;
    
    String country;
    
    String city;
    
    String district;
    
    String commune;

    String detailAddress;
    
    String userId;
}
