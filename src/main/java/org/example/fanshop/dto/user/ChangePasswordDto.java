package org.example.fanshop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    private String currentPassword;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 60, message = "Пароль должен быть от 8 до 60 символов")
    private String newPassword;
}