package org.example.fanshop.minio;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.entity.Image;
import org.example.fanshop.entity.Product;
import org.example.fanshop.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final ImageRepository imageRepository;

    @Value("${minio.bucketName}")
    private String bucketName;


    // 1️⃣ Загрузка изображения (без привязки к товару)
    public List<Image> uploadImage(List<MultipartFile> files, Product product) {
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files){
            try {
                String imageId = UUID.randomUUID().toString();

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(imageId)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                Image image = new Image();
                image.setMinioId(imageId);
                image.setOriginalFilename(file.getOriginalFilename());
                image.setProduct(product);
                images.add(imageRepository.save(image));
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при загрузке изображения", e);
            }
        }
        return images;
    }


    public List<String> getImagesByProductId(Long productId) {
        List<Image> images = imageRepository.findByProductId(productId);
        List<String> base64Images = new ArrayList<>();

        for (Image image : images) {
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(image.getMinioId())
                            .build()
            )) {
                byte[] bytes = stream.readAllBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                base64Images.add(base64);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при получении изображения: " + image.getMinioId(), e);
            }
        }
        return base64Images;
    }



//    // 2️⃣ Привязка изображений к товару
//    public void attachImagesToProduct(Long productId, List<String> imageIds) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Товар не найден"));
//
//        List<Image> images = imageRepository.findAllById(imageIds.stream().map(Long::parseLong).toList());
//        for (Image image : images) {
//            image.setProduct(product);
//        }
//        imageRepository.saveAll(images);
//    }

    // 3️⃣ Получение изображения
    public InputStream getImage(String imageId) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageId)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении изображения", e);
        }
    }

    // 4️⃣ Удаление изображения
    public void deleteImage(String imageId) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageId)
                            .build()
            );

            imageRepository.deleteById(Long.parseLong(imageId));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении изображения", e);
        }
    }
}
