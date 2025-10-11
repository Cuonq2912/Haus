package com.example.haus.domain.dto.response.product.momo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoCreateOrderResponseDto {

    String partnerCode;
    String orderId;
    String requestId;
    Long amount;
    Long responseTime;
    String message;
    String resultCode;
    String payUrl;
    String deeplink;
    String qrCodeUrl;

}