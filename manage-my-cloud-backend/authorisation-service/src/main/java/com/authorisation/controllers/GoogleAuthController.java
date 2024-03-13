package com.authorisation.controllers;

import com.authorisation.dto.UserDto;
import com.authorisation.response.GoogleDriveLinkResponse;
import com.authorisation.response.GoogleDriveLinkResponse;
import com.authorisation.services.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class GoogleAuthController {

    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/registergoogleuser")
    public ResponseEntity<UserDto> storeAuthCode(@RequestBody String authCode) {
        return googleAuthService.storeAuthCode(authCode);
    }

    @PostMapping("/link-google-account")
    public ResponseEntity<GoogleDriveLinkResponse> linkGoogleAccount(@RequestBody String authCode, @RequestParam("email") String email) {

        GoogleDriveLinkResponse response = googleAuthService.linkGoogleAccount(authCode, email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}