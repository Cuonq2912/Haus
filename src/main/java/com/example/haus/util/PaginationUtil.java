package com.example.haus.util;

import com.example.haus.domain.dto.pagination.PaginationCustom;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public class PaginationUtil {

  private PaginationUtil() {
  }

  public static <T> PaginationResponseDto<T> createPaginationResponse(
      Page<?> page,
      PaginationRequestDto request,
      List<T> items
  ) {
    PaginationCustom paginationCustom = PaginationCustom.builder()
        .pageNum(request.getDisplayPageNum()) // from 1
        .pageSize(request.getPageSize())
        .totalElement(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .sortBy(request.getSortBy())
        .sortType(request.getSortType())
        .build();

    return new PaginationResponseDto<>(paginationCustom, items);
  }
}