package org.example.fanshop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsResponse {
    private Long id;
    private String heading;
    private String description;
    private Long imageId;
}
