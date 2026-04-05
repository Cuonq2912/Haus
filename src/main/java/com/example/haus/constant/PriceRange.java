package com.example.haus.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PriceRange {
    @JsonProperty("under_1m")
    UNDER_1M(0d, 1000000d), @JsonProperty("from_1m_to_3m")
    FROM_1M_TO_3M(1000000d, 3000000d), @JsonProperty("from_3m_to_6m")
    FROM_3M_TO_6M(3000000d, 6000000d), @JsonProperty("from_6m_to_8m")
    FROM_6M_TO_8M(6000000d, 8000000d), @JsonProperty("above_8m")
    ABOVE_8M(8000000d, Double.MAX_VALUE);

    private final Double minPrice;
    private final Double maxPrice;

    PriceRange(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public static PriceRange fromString(String value) {
        for (PriceRange range : values()) {
            if (range.name().equalsIgnoreCase(value)) {
                return range;
            }
        }
        throw new IllegalArgumentException("Invalid PriceRange: " + value);
    }
}