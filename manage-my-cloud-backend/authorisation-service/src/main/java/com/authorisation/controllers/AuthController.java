package com.authorisation.controllers;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.EmailDto;
import com.authorisation.dto.UserDto;
import com.authorisation.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh-user")
    public ResponseEntity<UserDto> refreshUser(@RequestBody @Valid EmailDto emailDto) {
        UserDto userDto = userService.refreshUser(emailDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        return ResponseEntity.ok(userDto);
    }

}
