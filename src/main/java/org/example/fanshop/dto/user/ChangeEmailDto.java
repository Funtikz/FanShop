package org.example.fanshop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailDto {
    private String currentPassword;
    @NotBlank(message = "Почта не может быть пустой")
    @Email
    private String newEmail;
}
