package org.example.fanshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanshop.dto.NewsRequest;
import org.example.fanshop.dto.NewsResponse;
import org.example.fanshop.entity.News;
import org.example.fanshop.service.impl.NewsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsServiceImpl newsService;

    @PostMapping
    public ResponseEntity<Void> createNews(@RequestParam String heading,
                                           @RequestParam String description,
                                           @RequestParam MultipartFile image) {
        NewsRequest request = new NewsRequest();
        request.setHeading(heading);
        request.setDescription(description);
        request.setImage(image);

        newsService.addNewsWithImage(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNews(@PathVariable("id") Long id,
                                           @RequestParam String heading,
                                           @RequestParam String description,
                                           @RequestParam(required = false) MultipartFile image) {
        NewsResponse response = new NewsResponse();
        News byId = newsService.findById(id);
        response.setId(id);
        response.setImageId(byId.getImages().getId());
        response.setHeading(heading);
        response.setDescription(description);

        newsService.updateNews(response, image);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNewsById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        List<News> newsList = newsService.findAllNews();
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
    // Получение новости по ID (если нужно)
    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        News news = newsService.findById(id);
        return new ResponseEntity<>(news, HttpStatus.OK);
    }
}
