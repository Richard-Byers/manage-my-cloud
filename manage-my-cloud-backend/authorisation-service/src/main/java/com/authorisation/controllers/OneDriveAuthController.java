package com.authorisation.controllers;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.response.OneDriveTokenResponse;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.OneDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.authorisation.Constants.*;
import static com.authorisation.util.EncryptionUtil.decrypt;

@RequiredArgsConstructor
@RestController
public class OneDriveAuthController {

    private final OneDriveService oneDriveService;
    private  final CloudPlatformService cloudPlatformService;

    @GetMapping("/onedrive-store-tokens")
    public ResponseEntity<OneDriveTokenResponse> getAndStoreUserTokens(@RequestParam("code") String code, @RequestParam("email") String email) {

        OneDriveTokenResponse response = oneDriveService.getAndStoreUserTokens(code, email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/onedrive-refresh-access-token")
    public ResponseEntity<List<Pair<String, OneDriveTokenResponse>>> refreshAccessToken(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        List<CloudPlatform> cloudPlatforms = cloudPlatformService.getDriveEmailAndRefreshTokens(email, "OneDrive");
        List<Pair<String, OneDriveTokenResponse>> responses = new ArrayList<>();
        for (CloudPlatform cloudPlatform : cloudPlatforms) {
            if (cloudPlatform.getAccessTokenExpiryDate().getTime() - System.currentTimeMillis() <= EXPIRATION_THRESHOLD_MILLISECONDS) {
                String refreshToken = decrypt(cloudPlatform.getRefreshToken());
                OneDriveTokenResponse response = oneDriveService.refreshToken(refreshToken, cloudPlatform.getDriveEmail(), email);
                responses.add(Pair.of("Token refreshed for " + cloudPlatform.getDriveEmail(), response));
            } else {
                OneDriveTokenResponse defaultResponse = new OneDriveTokenResponse();  // Create a new OneDriveTokenResponse with default values
                responses.add(Pair.of("Token still valid for " + cloudPlatform.getDriveEmail(), defaultResponse));
            }
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
