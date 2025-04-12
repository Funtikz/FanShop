package org.example.fanshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.fanshop.dto.user.*;
import org.example.fanshop.service.impl.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private UserServiceImpl userServiceImpl;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDto> createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        return new ResponseEntity<>(userServiceImpl.addUser(userRegistrationDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserAuthDto userCredentialDto , HttpServletResponse response) {
        return new ResponseEntity<>(userServiceImpl.login(userCredentialDto, response), HttpStatus.OK);
    }

    @Operation(summary = "Посмотреть информацию о текущем авторизированном пользователе")
    @GetMapping("/current-user")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal Object principal) {
        return  new ResponseEntity<>(userServiceImpl.toDto(userServiceImpl.getCurrentUser(principal)), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        userServiceImpl.logout(response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Сменить пароль")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal Object principal,
                                            @RequestBody @Valid ChangePasswordDto dto) {
        Long userId = userServiceImpl.getCurrentUser(principal).getId();
        userServiceImpl.changePassword(userId, dto);
        return ResponseEntity.ok("Пароль успешно изменён");
    }

    @Operation(summary = "Сменить email")
    @PutMapping("/change-email")
    public ResponseEntity<?> changeEmail(@AuthenticationPrincipal Object principal,
                                         @RequestBody @Valid ChangeEmailDto dto) {
        Long userId = userServiceImpl.getCurrentUser(principal).getId();
        userServiceImpl.changeEmail(userId, dto);
        return ResponseEntity.ok("Email успешно изменён");
    }

    @Operation(summary = "Сменить номер телефона")
    @PutMapping("/change-phone")
    public ResponseEntity<?> changePhone(@AuthenticationPrincipal Object principal,
                                         @RequestBody @Valid ChangePhoneDto dto) {
        Long userId = userServiceImpl.getCurrentUser(principal).getId();
        userServiceImpl.changePhoneNumber(userId, dto);
        return ResponseEntity.ok("Номер телефона успешно изменён");
    }

    @Operation(summary = "Просто так надо :) ")
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
