package com.example.haus.constant.promotion;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PromotionType {

    @JsonProperty("order")
    ORDER,
    @JsonProperty("category")
    CATEGORY
}
