package org.example.fanshop.dto;

import lombok.Data;
import org.example.fanshop.entity.enums.ClothingSize;

@Data
public class CartItemRemoveRequest {
    private Long productId;
    private Integer quantity;
    private ClothingSize clothingSize;
}
