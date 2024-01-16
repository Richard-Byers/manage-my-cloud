package com.authorisation.controllers;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import org.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStreamReader;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class OAuthAuthorisationController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @GetMapping("/csrf-token-endpoint")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return csrfToken.getToken();
    }

    @PostMapping("/storetoken")
    public ResponseEntity<UserDto> storeAuthCode( @RequestBody String authCode) {

        String jsonString = authCode.substring(authCode.indexOf("{"));
        JSONObject jsonObject = new JSONObject(jsonString);
        String authCodeOutput = jsonObject.getString("authCode");

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
                            authCodeOutput,
                            "postmessage")
                            .execute();



            String accessToken = tokenResponse.getAccessToken();

            String idTokenStr = tokenResponse.getIdToken();
            GoogleIdToken idToken = GoogleIdToken.parse(JacksonFactory.getDefaultInstance(), idTokenStr);
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            System.out.println("Access Token: " + accessToken);
            System.out.println("Email: " + email);

            userService.registerGoogleUser(email, accessToken);

            UserDto userDto = userService.googleLogin(email);
            userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

            return ResponseEntity.ok(userDto);
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
