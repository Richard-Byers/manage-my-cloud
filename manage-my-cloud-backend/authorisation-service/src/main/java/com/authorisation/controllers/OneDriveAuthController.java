package com.authorisation.controllers;

import com.authorisation.response.OneDriveTokenResponse;
import com.authorisation.services.OneDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
public class OneDriveAuthController {

    private final OneDriveService oneDriveService;
    @GetMapping("/onedrive-store-tokens")
    public ResponseEntity<OneDriveTokenResponse> getAndStoreUserTokens(@RequestParam("code") String code, @RequestParam("email") String email) {

        OneDriveTokenResponse response = oneDriveService.getAndStoreUserTokens(code, email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
