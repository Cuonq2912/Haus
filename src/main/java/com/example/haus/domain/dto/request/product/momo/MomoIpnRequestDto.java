package com.example.haus.domain.dto.request.product.momo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoIpnRequestDto {

    String partnerCode;
    String orderId;
    String requestId;
    Long amount;
    String orderInfo;
    String orderType;
    String transId;
    String resultCode;
    String message;
    String payType;
    Long responseTime;
    String extraData;
    String signature;

}