package com.example.haus.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderStatus {

    @JsonProperty("pending")
    PENDING, @JsonProperty("confirmed")
    CONFIRMED, @JsonProperty("processing")
    PROCESSING, @JsonProperty("completed")
    COMPLETED, @JsonProperty("failed")
    FAIL, @JsonProperty("delivered")
    DELIVERED, @JsonProperty("cancelled")
    CANCELLED, @JsonProperty("returned")
    RETURNED, @JsonProperty("refunded")
    REFUNDED;
}
