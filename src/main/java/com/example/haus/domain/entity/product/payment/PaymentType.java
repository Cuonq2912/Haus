package com.example.haus.domain.entity.product.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PaymentType {
    @JsonProperty("cash_on_delivery")
    COD,

    @JsonProperty("bank_transfer")
    BANK_TRANSFER,
    
    @JsonProperty("online_payment")
    ONLINE_PAYMENT;
}