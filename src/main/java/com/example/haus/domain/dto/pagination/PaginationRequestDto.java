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

    @Parameter(description = "Page you want to retrieve (0..N)")
    Integer pageNum = CommonConstant.ZERO_INT_VALUE;

    @Parameter(description = "Number of records per page.")
    Integer pageSize = CommonConstant.ZERO_INT_VALUE;

    public int getPageNum() {
        if (pageNum < 1) {
            pageNum = CommonConstant.ONE_INT_VALUE;
        }
        return pageNum - 1;
    }

    public int getPageSize() {
        if (pageSize < 1) {
            pageSize = CommonConstant.PAGE_SIZE_DEFAULT;
        }
        return pageSize;
    }
}
