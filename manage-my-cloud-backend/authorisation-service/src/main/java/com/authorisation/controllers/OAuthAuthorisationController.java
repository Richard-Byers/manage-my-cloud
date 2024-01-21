package com.authorisation.controllers;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;

@CrossOrigin(origins = "*")
@RestController
public class OAuthAuthorisationController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @PostMapping("/registergoogleuser")
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



            String refreshToken = tokenResponse.getRefreshToken();

            String idTokenStr = tokenResponse.getIdToken();
            GoogleIdToken idToken = GoogleIdToken.parse(JacksonFactory.getDefaultInstance(), idTokenStr);
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            userService.registerGoogleUser(email, firstName, lastName, pictureUrl, refreshToken);

            UserDto userDto = userService.googleLogin(email);
            userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }
}
