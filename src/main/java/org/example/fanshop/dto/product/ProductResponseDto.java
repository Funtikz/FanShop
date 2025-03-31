package org.example.fanshop.dto.product;

import lombok.Data;
import org.example.fanshop.entity.enums.ProductType;

import java.util.List;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private ProductType productType;
    private Double originalPrice;
    private Integer discountPercentage;
    private Double discountedPrice;
    private List<ProductVariantResponseDto> variants;
}
