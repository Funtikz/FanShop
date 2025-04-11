package org.example.fanshop.service.impl;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.NewsRequest;
import org.example.fanshop.dto.NewsResponse;
import org.example.fanshop.entity.Image;
import org.example.fanshop.entity.News;
import org.example.fanshop.minio.MinioService;
import org.example.fanshop.repository.ImageRepository;
import org.example.fanshop.repository.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class NewsServiceImpl {
    private final NewsRepository repository;
    private final ImageRepository imageRepository;
    private final MinioService minioService;

    @Transactional
    public void addNewsWithImage(NewsRequest request) {
        try {
            Image image = minioService.uploadSingleImage(request.getImage());
            News news = new News();
            news.setHeading(request.getHeading());
            news.setDescription(request.getDescription());
            news.setDate(LocalDateTime.now());
            news.setImages(image);
            repository.save(news);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении новости с изображением", e);
        }
    }

    @Transactional
    public void updateNews(NewsResponse response, MultipartFile newImage) {
        // Находим существующую новость по ID
        News existingNews = findById(response.getId());

        // Обновляем описание и заголовок
        existingNews.setDescription(response.getDescription());
        existingNews.setHeading(response.getHeading());
        existingNews.setDate(LocalDateTime.now());

        // Если новое изображение было передано, загружаем его в Minio и обновляем в сущности
        if (newImage != null && !newImage.isEmpty()) {
            try {
                // Если у новости уже есть изображение, удаляем старое из Minio
                Image oldImage = existingNews.getImages();
                if (oldImage != null) {
                    // Удаляем старое изображение из Minio
                    minioService.deleteImage(oldImage.getMinioId());
                    // Также удаляем старое изображение из базы данных
                    imageRepository.delete(oldImage);
                }

                // Загружаем новое изображение в Minio
                Image newImageEntity = minioService.uploadSingleImage(newImage);

                // Обновляем изображение новости
                existingNews.setImages(newImageEntity);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при обновлении изображения", e);
            }
        }

        // Сохраняем обновленную новость
        repository.save(existingNews);
    }


    @Transactional
    public void deleteNewsById(Long id){
        News news = findById(id);
        minioService.deleteImage(news.getImages().getMinioId());
        repository.delete(news);
    }

    public List<News> findAllNews(){
        return repository.findAll();
    }

    public News findById(Long id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException());
    }

    News toEntity(NewsRequest request){
        News news = new News();
        news.setDescription(request.getDescription());
        news.setHeading(request.getHeading());
        return  news;
    }


}
