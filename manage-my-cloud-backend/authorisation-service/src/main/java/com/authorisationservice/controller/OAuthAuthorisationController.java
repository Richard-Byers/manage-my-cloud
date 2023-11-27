package com.authorisationservice.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class OAuthAuthorisationController {
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @GetMapping("/csrf-token-endpoint")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return csrfToken.getToken();
    }

    @PostMapping("/storetoken")
    public ResponseEntity<Map<String, String>> storeAuthCode(@RequestHeader("X-Requested-With") String requestedWith, @RequestBody String authCode) {
        if (requestedWith == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid request header");
        }

        try {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new InputStreamReader(getClass().getResourceAsStream(CREDENTIALS_FILE_PATH)));
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://www.googleapis.com/oauth2/v4/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        authCode,
                        "postmessage")
                        .execute();

        //Need to store the tokenResponse somewhere in DB or memory
            String accessToken = tokenResponse.getAccessToken();

            //Just was testing it out
//            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//            Drive drive =
//                    new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
//                            .setApplicationName("Auth Code Exchange Demo")
//                            .build();
//
//            About about = drive.about().get().setFields("*").execute();
            return ResponseEntity.ok(Collections.singletonMap("message", "Logged In"));
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }

    @GetMapping("/getDetails")
    public Map<String, Object> getAbout(GoogleCredential credential) {
        Map<String, Object> response = new HashMap<>();
        try {
            Drive drive =
                    new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                            .setApplicationName("Auth Code Exchange Demo")
                            .build();

            About about = drive.about().get().setFields("*").execute();
            response.put("about", about);

        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            response.put("error", "Error: " + e.getMessage());
        }
        return response;
    }
}
