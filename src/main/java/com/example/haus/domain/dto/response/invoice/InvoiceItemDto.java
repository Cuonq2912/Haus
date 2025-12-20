package com.example.haus.domain.dto.response.invoice;

import com.example.haus.domain.dto.response.product.MediaResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class InvoiceItemDto {
    //Product
    private Long productId;
    private String productCode;
    private String productName;
    private String description;

    // ProductVariant
    private Long productVariationId;
    private Integer inventoryQuantity;
    private Double price;
    private Double total;

    private String color;
    private String size;

    private MediaResponseDto media;
}