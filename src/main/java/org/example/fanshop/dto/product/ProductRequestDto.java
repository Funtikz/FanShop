package org.example.fanshop.dto.product;

import lombok.Data;
import org.example.fanshop.entity.enums.ProductType;

import java.util.List;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 2, max = 255, message = "Название должно содержать от 2 до 255 символов")
    private String name;

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Тип товара обязателен")
    private ProductType productType;

    @NotNull(message = "Оригинальная цена обязательна")
    @Positive(message = "Цена должна быть больше 0")
    private Double originalPrice;

    @Min(value = 0, message = "Скидка не может быть меньше 0%")
    @Max(value = 100, message = "Скидка не может быть больше 100%")
    private Integer discountPercentage;

    private List<MultipartFile> images;

    @NotEmpty(message = "Должен быть хотя бы один вариант товара")
    private List<ProductVariantRequestDto> variants;
}

