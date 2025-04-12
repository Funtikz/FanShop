package org.example.fanshop.service.impl;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.NewsResponse;
import org.example.fanshop.dto.product.ProductRequestDto;
import org.example.fanshop.dto.product.ProductResponseDto;
import org.example.fanshop.dto.product.ProductVariantRequestDto;
import org.example.fanshop.dto.product.ProductVariantResponseDto;
import org.example.fanshop.entity.Image;
import org.example.fanshop.entity.News;
import org.example.fanshop.entity.Product;
import org.example.fanshop.entity.ProductVariant;
import org.example.fanshop.entity.enums.ProductType;
import org.example.fanshop.minio.MinioService;
import org.example.fanshop.repository.ImageRepository;
import org.example.fanshop.repository.ProductRepository;
import org.example.fanshop.repository.ProductVariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final MinioService minioService;
    private final ImageRepository imageRepository;
    private final ProductVariantRepository variantRepository;


    @Transactional
    public void updateProduct(ProductResponseDto response, List<MultipartFile> images) {
        // Находим существующий товар по ID
        Product existingProduct = findById(response.getId());

        // Обновляем информацию о товаре
        existingProduct.setProductType(response.getProductType());
        existingProduct.setName(response.getName());
        existingProduct.setDescription(response.getDescription());
        existingProduct.setOriginalPrice(response.getOriginalPrice());
        existingProduct.setDiscountPercentage(response.getDiscountPercentage());
        existingProduct.setDiscountedPrice(response.getDiscountedPrice());

        // Обновляем варианты товара
        List<ProductVariantResponseDto> variants = response.getVariants();
        if (variants != null && !variants.isEmpty()) {
            // Удаляем старые варианты
            existingProduct.getVariants().clear();

            // Добавляем новые варианты из DTO
            for (ProductVariantResponseDto variantDto : variants) {
                ProductVariant variant = new ProductVariant();
                variant.setSize(variantDto.getSize());
                variant.setQuantity(variantDto.getQuantity());
                variant.setProduct(existingProduct); // Связываем вариант с продуктом
                existingProduct.getVariants().add(variant); // Добавляем новый вариант
            }
        }

        // Обновляем изображения товара, если они есть
        if (images != null && !images.isEmpty()) {
            // Удаляем старые изображения из Minio, если они есть
            List<Image> existingImages = existingProduct.getImages();
            if (existingImages != null && !existingImages.isEmpty()) {
                for (Image oldImage : existingImages) {
                    try {
                        minioService.deleteImage(oldImage.getMinioId());  // Удаляем старое изображение из Minio
                        imageRepository.delete(oldImage);  // Удаляем старое изображение из базы данных
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка при удалении изображения", e);
                    }
                }
            }

            // Загружаем новые изображения
            List<Image> newImages = new ArrayList<>();
            for (MultipartFile image : images) {
                try {
                    // Загружаем каждое изображение в Minio

                    Image uploadedImage = minioService.uploadSingleImageForProduct(image, existingProduct);
                    uploadedImage.setProduct(existingProduct);
                    newImages.add(uploadedImage);

                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при загрузке изображения", e);
                }
            }

            // Обновляем список изображений товара
            existingProduct.setImages(newImages);
        }

        // Сохраняем обновленный товар
        repository.save(existingProduct);
    }

    @Transactional
    public void deleteProductById(Long id) {
        // Находим существующий товар по ID
        Product product = findById(id);

        // Удаляем все изображения товара из Minio
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (Image image : product.getImages()) {
                try {
                    // Удаляем изображение из Minio
                    minioService.deleteImage(image.getMinioId());
                    // Удаляем изображение из базы данных
                    imageRepository.delete(image);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при удалении изображения из Minio", e);
                }
            }
        }

        // Удаляем все варианты товара из базы данных (если необходимо)
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (ProductVariant variant : product.getVariants()) {
                variantRepository.delete(variant);
            }
        }

        // Удаляем сам товар из базы данных
        repository.delete(product);
    }


    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto){
        Product product = toEntity(productRequestDto);
        product.setDiscountedPrice(calculateDiscountedPrice(product.getOriginalPrice(), product.getDiscountPercentage()));
        Product save = repository.save(product);
        List<Image> images = minioService.uploadImage(productRequestDto.getImages(), save);
        save.setImages(images);
        Product save1 = repository.save(save);
        return toDto(save1);
    }

    public List<ProductResponseDto> getAll(){
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<String> getProductImages(Long productId) {
        return minioService.getImagesByProductId(productId);
    }

    public List<ProductResponseDto> findAllByProductType(ProductType type){
        return repository.findAllByProductType(type).stream().map(this::toDto).collect(Collectors.toList());
    }


    public ProductResponseDto getById(Long id){
        return toDto(findById(id));
    }

    public Product findById(Long id){
        return  repository.findById(id).orElseThrow(() -> new UsernameNotFoundException(""));
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

    public Double calculateDiscountedPrice(Double originalPrice, Integer discountPercentage) {
        double discountAmount = originalPrice * (discountPercentage / 100.0);
        return originalPrice - discountAmount;
    }


}
