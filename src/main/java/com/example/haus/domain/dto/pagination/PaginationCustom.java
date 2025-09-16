package com.example.haus.domain.dto.pagination;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationCustom {

    Integer pageNum;
    Integer pageSize;

    Long totalElement;
    Integer totalPages;

    String sortBy;
    String sortType;

}
