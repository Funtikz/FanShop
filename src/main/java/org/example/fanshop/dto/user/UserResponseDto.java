package org.example.fanshop.dto.user;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDto {
    private Long id;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
