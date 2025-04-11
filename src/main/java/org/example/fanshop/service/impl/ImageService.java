package org.example.fanshop.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.minio.MinioService;
import org.example.fanshop.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final MinioService minioService;

    public List<String> getImageById(Long id){
        return minioService.getImagesByImageId(id);
    }

}
