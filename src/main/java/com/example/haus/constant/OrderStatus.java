package com.example.haus.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderStatus {

    @JsonProperty("pending")
    PENDING,
    @JsonProperty("confirmed")
    CONFIRMED,
    @JsonProperty("processing")
    PROCESSING,
    @JsonProperty("shipped")
    SHIPPED,
    @JsonProperty("out for delivery")
    OUT_FOR_DELIVERY,
    @JsonProperty("delivered")
    DELIVERED,
    @JsonProperty("cancelled")
    CANCELLED,
    @JsonProperty("returned")
    RETURNED,
    @JsonProperty("refunded")
    REFUNDED;
}
