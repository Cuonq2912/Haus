package com.example.haus.constant.promotion;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PromotionType {

    @JsonProperty("order")
    ORDER,
    @JsonProperty("category")
    CATEGORY;

    public static PromotionType fromString(String value) {
        if (value == null) return null;
        for (PromotionType type : PromotionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
