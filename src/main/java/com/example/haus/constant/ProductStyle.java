package com.example.haus.constant;

public enum ProductStyle {
  TU_QUAN_AO("Tủ quần áo"),
  GIUONG("Giường"),
  TU_KE_SACH("Tủ kệ sách"),
  TU("Tủ"),
  BAN_TRA("Bàn trà"),
  BAN_DAU_GIUONG("Bàn đầu giường"),
  TU_DAU_GIUONG("Tủ đầu giường"),
  QUAT_TRAN("Quạt trần"),
  GHE("Ghế"),
  SOFA("Sofa"),
  KE_TIVI("Kệ tivi");

  private final String displayName;

  ProductStyle(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}