package com.example.haus.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ProductColor {
  @JsonProperty("kem_nau")
  KEM_NAU("Kem Nâu"),
  @JsonProperty("hong")
  HONG("Hồng"),
  @JsonProperty("den")
  DEN("Đen"),
  @JsonProperty("cam")
  CAM("Cam"),
  @JsonProperty("vang")
  VANG("Vàng"),
  @JsonProperty("xanh_duong")
  XANH_DUONG("Xanh dương"),
  @JsonProperty("do")
  DO("Đỏ"),
  @JsonProperty("xanh_la_cay")
  XANH_LA_CAY("Xanh lá cây"),
  @JsonProperty("tim")
  TIM("Tím");

  private final String displayName;

  ProductColor(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}