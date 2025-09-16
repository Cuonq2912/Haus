package com.example.haus.constant;

public enum PriceRange {
  UNDER_1M(0, 1000000),
  FROM_1M_TO_3M(1000000, 3000000),
  FROM_3M_TO_6M(3000000, 6000000),
  FROM_6M_TO_8M(6000000, 8000000),
  ABOVE_8M(8000000, Double.MAX_VALUE);

  private final double minPrice;
  private final double maxPrice;

  PriceRange(double minPrice, double maxPrice) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
  }

  public double getMinPrice() {
    return minPrice;
  }

  public double getMaxPrice() {
    return maxPrice;
  }
}