package org.example.fanshop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePhoneDto {
    private String currentPassword;
    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = "^\\+?[0-9]{10,13}$", message = "Номер телефона должен быть в формате +7XXXXXXXXXX")
    private String newPhoneNumber;
}