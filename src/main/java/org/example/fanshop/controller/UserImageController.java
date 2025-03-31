package org.example.fanshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.fanshop.service.impl.UserImageServiceImpl;
import org.example.fanshop.dto.user.ImageUploadDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/images")
@Repository
@RequiredArgsConstructor
public class UserImageController {

    private final UserImageServiceImpl imageService;

    @Operation(summary = "Обновить изображение профиля")
    @PutMapping("/update")
    public ResponseEntity<String> updateImage(@RequestParam("userId") Long userId,
                                              @RequestParam("images")MultipartFile images){
        ImageUploadDto imageUpdateDto = new ImageUploadDto();
        imageUpdateDto.setId(userId);
        imageUpdateDto.setImage(images);
        imageService.changeProfileImage(imageUpdateDto);
        return ResponseEntity.ok("Картинка успешна обновлена!");
    }

    @Operation(summary = "Получить текущую аватарку пользователя")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        try {
            byte[] imageBytes = imageService.getProfileByUserId(userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Удалить аватарку и поставить дефолтную")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteProfileAndUploadDefault(@PathVariable("userId") Long userId){
        imageService.deleteProfileImageAndUploadDefault(userId);
        return ResponseEntity.ok("Изображения профиля удалено");
    }

}
