package org.example.fanshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanshop.dto.product.ProductRequestDto;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.entity.enums.ProductType;
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
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable("id") Long id,
                                              @Valid @RequestPart(value = "productData") String productData,
                                              @RequestPart(value = "images") List<MultipartFile> images){

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductResponseDto productResponseDto = objectMapper.readValue(productData, ProductResponseDto.class);
            productResponseDto.setId(id);
            productResponseDto.setDiscountedPrice(productService.calculateDiscountedPrice(productResponseDto.getOriginalPrice(), productResponseDto.getDiscountPercentage()));
            // Вызываем сервис для обновления товара
            productService.updateProduct(productResponseDto, images);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            log.error("Ошибка при создании продукта: " + e.getMessage());
        }
        throw  new ValidationException();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PostMapping("/create")
    @Operation(summary = "Создание продукта")
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

    @GetMapping("get-all")
    public ResponseEntity<List<ProductResponseDto>> getAll(){
        List<ProductResponseDto> all = productService.getAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @Operation(summary = "Получение изображений продукта по id")
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long productId) {
        List<String> images = productService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable("id") Long id){
        return new ResponseEntity<>(productService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/by-type")
    public List<ProductResponseDto> findAllByProductType(@RequestParam ProductType type) {
        return productService.findAllByProductType(type);
    }

}
