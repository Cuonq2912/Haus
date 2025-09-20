package com.example.haus.domain.dto.pagination;

import com.example.haus.constant.CommonConstant;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationRequestDto {

    @Parameter(description = "Page you want to retrieve (1..N)")
    Integer pageNum = CommonConstant.ZERO_INT_VALUE;

    @Parameter(description = "Number of records per page.")
    Integer pageSize = CommonConstant.ZERO_INT_VALUE;

    @Parameter(description = "Field to sort by (name, price, createdDate)")
    String sortBy;

    @Parameter(description = "Sort direction (ASC, DESC)")
    String sortType;

    public int getPageNum() {
        if (pageNum == null || pageNum < 1) {
            return 0;
        }
        return pageNum - 1;
    }

    public int getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return CommonConstant.PAGE_SIZE_DEFAULT;
        }
        return pageSize;
    }

    public int getDisplayPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    public String getSortBy() {
        return sortBy != null && !sortBy.trim().isEmpty() ? sortBy.trim() : "name";
    }

    public String getSortType() {
        if (sortType == null || sortType.trim().isEmpty()) {
            return CommonConstant.SORT_TYPE_ASC;
        }
        String normalized = sortType.trim().toUpperCase();
        return normalized.equals("DESC") ? CommonConstant.SORT_TYPE_DESC : CommonConstant.SORT_TYPE_ASC;
    }

    public PaginationRequestDto(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
