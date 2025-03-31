package org.example.fanshop.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.product.ProductRequestDto;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.dto.product.ProductVariantRequestDto;
import org.example.fanshop.dto.product.ProductVariantResponseDto;
import org.example.fanshop.entity.Image;
import org.example.fanshop.entity.Product;
import org.example.fanshop.entity.ProductVariant;
import org.example.fanshop.minio.MinioService;
import org.example.fanshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final MinioService minioService;

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto){
        Product product = toEntity(productRequestDto);
        product.setDiscountedPrice(calculateDiscountedPrice(product.getOriginalPrice(), product.getDiscountPercentage()));
        Product save = repository.save(product);
        List<Image> images = minioService.uploadImage(productRequestDto.getImages(), save);
        save.setImages(images);
        Product save1 = repository.save(save);
        return toDto(save1);
    }

    public List<String> getProductImages(Long productId) {
        return minioService.getImagesByProductId(productId);
    }



    public Product toEntity(ProductRequestDto dto){
        Product product = new Product();
        product.setProductType(dto.getProductType());
        product.setDescription(dto.getDescription());
        product.setName(dto.getName());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setDiscountPercentage(dto.getDiscountPercentage());
        product.setImages(null);
        List<ProductVariantRequestDto> variantsDto = dto.getVariants();
        List<ProductVariant> variants = new ArrayList<>();
        for (ProductVariantRequestDto variantRequestDto : variantsDto){
            ProductVariant productVariant = new ProductVariant();
            productVariant.setSize(variantRequestDto.getSize());
            productVariant.setQuantity(variantRequestDto.getQuantity());
            productVariant.setProduct(product);
            variants.add(productVariant);
        }

        product.setVariants(variants);
        return product;
    }

    private List<ProductVariantResponseDto> listToDto(List<ProductVariant> variants){
        List<ProductVariantResponseDto> variantsDto = new ArrayList<>();
        for (ProductVariant entity : variants){
            ProductVariantResponseDto productVariant = new ProductVariantResponseDto();
            productVariant.setSize(entity.getSize());
            productVariant.setQuantity(entity.getQuantity());
            productVariant.setId(entity.getId());
            variantsDto.add(productVariant);
        }
        return variantsDto;
    }

    private ProductResponseDto toDto(Product product){
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setVariants(listToDto(product.getVariants()));
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setProductType(product.getProductType());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setOriginalPrice(product.getOriginalPrice());
        return dto;
    }

    private Double calculateDiscountedPrice(Double originalPrice, Integer discountPercentage) {
        double discountAmount = originalPrice * (discountPercentage / 100.0);
        return originalPrice - discountAmount;
    }


}
