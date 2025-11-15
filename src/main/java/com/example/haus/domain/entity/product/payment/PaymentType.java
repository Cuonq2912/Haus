package com.example.haus.domain.entity.product.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentType {
    @JsonProperty("CASH_ON_DELIVERY")
    COD,

    @JsonProperty("ONLINE_PAYMENT")
    ONLINE_PAYMENT;
}