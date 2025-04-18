package org.example.fanshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.fanshop.service.impl.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService service;

    @GetMapping("{id}")
    public ResponseEntity<List<String>> getImageById(@PathVariable("id") Long id){
        List<String> imageById = service.getImageById(id);
        return new ResponseEntity<>(imageById, HttpStatus.OK);
    }
}
