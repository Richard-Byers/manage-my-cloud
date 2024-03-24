package com.authorisation.controllers;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.EmailDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.RefreshToken;
import com.authorisation.requests.RefreshTokenRequest;
import com.authorisation.response.JwtResponse;
import com.authorisation.services.RefreshTokenService;
import com.authorisation.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        userService.updateFirstLogin(userDto.getEmail());
        RefreshToken refreshTokenObject = refreshTokenService.createRefreshtoken(userDto.getEmail());
        userDto.setRefreshToken(refreshTokenObject.getToken());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh-user")
    public ResponseEntity<UserDto> refreshUser(@RequestBody @Valid EmailDto emailDto) {
        UserDto userDto = userService.refreshUser(emailDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        RefreshToken refreshTokenObject = refreshTokenService.createRefreshtoken(userDto.getEmail());
        userDto.setRefreshToken(refreshTokenObject.getToken());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Refresh token is not in the database or has expired!"));

        String email = refreshToken.getUserEntity().getEmail();
        //Generate new tokens, refresh is single use so we just refresh here
        String accessToken = userAuthenticationProvider.createToken(email);
        String newRefreshToken = refreshTokenService.createRefreshtoken(email).getToken();

        JwtResponse jwtResponse = new JwtResponse(accessToken, newRefreshToken);

        return ResponseEntity.ok(jwtResponse);
    }

}
