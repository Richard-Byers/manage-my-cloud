package com.authorisation.controllers;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.services.UserService;
import com.authorisation.services.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;

@CrossOrigin(origins = "*")
@RestController
public class GoogleAuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @PostMapping("/registergoogleuser")
    public ResponseEntity<UserDto> storeAuthCode(@RequestBody String authCode) {
        return googleAuthService.storeAuthCode(authCode);
    }
}