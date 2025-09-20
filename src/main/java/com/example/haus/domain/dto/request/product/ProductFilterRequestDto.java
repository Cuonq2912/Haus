package com.example.haus.domain.dto.request.product;

import com.example.haus.constant.PriceRange;
import com.example.haus.constant.ProductColor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequestDto {

  @Schema(description = "Danh sách khoảng giá")
  List<PriceRange> priceRanges;

  @Schema(description = "Danh sách màu sắc")
  List<ProductColor> colors;

  @Schema(description = "Category Id")
  Long categoryId;

  @Schema(description = "Từ khóa tìm kiếm (tùy chọn)")
  String keyword;
}