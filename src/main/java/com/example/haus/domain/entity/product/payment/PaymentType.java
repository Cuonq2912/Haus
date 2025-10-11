package com.example.haus.domain.entity.product.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PaymentType {
    @JsonProperty("cash_on_delivery")
    COD,

    @JsonProperty("online_payment")
    ONLINE_PAYMENT;
}