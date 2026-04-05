package com.example.haus.constant.promotion;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PromotionStatus {
    @JsonProperty("active")
    ACTIVE, @JsonProperty("inactive")
    INACTIVE, @JsonProperty("expired")
    EXPIRED;

    public static PromotionStatus fromString(String value) {
        if (value == null)
            return null;
        for (PromotionStatus status : PromotionStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
