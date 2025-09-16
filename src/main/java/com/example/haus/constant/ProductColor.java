package com.example.haus.constant;

public enum ProductColor {
  KEM_NAU("Kem Nâu"),
  HONG("Hồng"),
  DEN("Đen"),
  CAM("Cam"),
  VANG("Vàng"),
  XANH_DUONG("Xanh dương"),
  DO("Đỏ"),
  XANH_LA_CAY("Xanh lá cây"),
  TIM("Tím");

  private final String displayName;

  ProductColor(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}