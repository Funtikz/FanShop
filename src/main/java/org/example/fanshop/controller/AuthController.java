package org.example.fanshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.fanshop.dto.user.UserAuthDto;
import org.example.fanshop.dto.user.UserRegistrationDto;
import org.example.fanshop.dto.user.UserResponseDto;
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

    @Operation(summary = "Просто так надо :) ")
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
}
