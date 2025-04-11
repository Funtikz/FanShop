package org.example.fanshop.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NewsRequest {
    private String heading;
    private String description;
    private MultipartFile image;
}
