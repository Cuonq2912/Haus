package com.example.haus.domain.dto.pagination;

import com.example.haus.constant.CommonConstant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationCustom {

    Integer pageNum;
    Integer pageSize;

    Long totalElement;
    Integer totalPages;

    String sortBy;
    String sortType;

}
