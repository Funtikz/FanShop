package org.example.fanshop.dto;

import lombok.Data;
import org.example.fanshop.entity.enums.ClothingSize;

@Data
public class CartItemResponseDto {
    private Long id;

    private Long cartId;

    private String name;

    private Long productId;

    private int quantity;

    private ClothingSize clothingSize;

    private double price;
}
