package org.example.fanshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.fanshop.entity.enums.ClubRole;
import org.example.fanshop.entity.enums.PlayerPosition;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PlayerRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    private String firstname;

    @NotBlank(message = "Фамилия не должна быть пустой")
    private String lastname;

    @NotNull(message = "Роль обязательна для заполнения")
    private ClubRole role;

    private PlayerPosition playerPosition;

    @NotNull(message = "Изображение обязательно для загрузки")
    private MultipartFile image;
}
