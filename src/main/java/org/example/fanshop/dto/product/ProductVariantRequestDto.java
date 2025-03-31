package org.example.fanshop.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.fanshop.entity.enums.ClothingSize;

@Data
public class ProductVariantRequestDto {
    private ClothingSize size;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть хотя бы 1")
    private Integer quantity;
}