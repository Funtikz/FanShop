package org.example.fanshop.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageUploadDto {
    private Long id;
    private MultipartFile image;
}
