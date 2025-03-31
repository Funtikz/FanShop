package org.example.fanshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanshop.dto.product.ProductRequestDto;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.service.impl.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestPart(value = "productData") String productData,
            @RequestPart(value = "images") List<MultipartFile> images
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductRequestDto productRequestDto = objectMapper.readValue(productData, ProductRequestDto.class);

            productRequestDto.setImages(images);

            return new ResponseEntity<>(productService.createProduct(productRequestDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Ошибка при создании продукта: " + e.getMessage());
        }
        throw  new ValidationException();
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long productId) {
        List<String> images = productService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }


}
