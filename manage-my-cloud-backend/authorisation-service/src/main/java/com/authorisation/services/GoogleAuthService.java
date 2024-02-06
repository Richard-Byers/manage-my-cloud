package com.authorisation.services;

import com.authorisation.Constants;
import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.authorisation.Constants.GOOGLEDRIVE;

@AllArgsConstructor
@Service
public class GoogleAuthService {

    private final CloudPlatformService cloudPlatformService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    public ResponseEntity<UserDto> storeAuthCode(String authCode) {
        String jsonString = authCode.substring(authCode.indexOf("{"));
        JSONObject jsonObject = new JSONObject(jsonString);
        String authCodeOutput = jsonObject.getString("authCode");

        try {
            GoogleTokenResponse tokenResponse = getGoogleTokenResponse(authCodeOutput);

            String idTokenStr = tokenResponse.getIdToken();
            GoogleIdToken idToken = GoogleIdToken.parse(JacksonFactory.getDefaultInstance(), idTokenStr);
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            userService.registerGoogleUser(email, firstName, lastName, pictureUrl);

            UserDto userDto = userService.googleLogin(email);
            userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }

    public GoogleTokenResponse getGoogleTokenResponse(String authCodeOutput) throws IOException {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new InputStreamReader(getClass().getResourceAsStream(Constants.GOOGLE_CREDENTIALS_FILE_PATH)));
        return new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                "https://www.googleapis.com/oauth2/v4/token",
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                authCodeOutput,
                "postmessage")
                .execute();
    }

    public GoogleTokenResponse linkGoogleAccount(String authCode, String email) {
        String jsonString = authCode.substring(authCode.indexOf("{"));
        JSONObject jsonObject = new JSONObject(jsonString);
        String authCodeOutput = jsonObject.getString("authCode");

        try {
            GoogleTokenResponse tokenResponse = getGoogleTokenResponse(authCodeOutput);
            storeUserPlatformLink(tokenResponse, email);

            return tokenResponse;
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }

    public void storeUserPlatformLink(GoogleTokenResponse tokenResponse, String email) {
        cloudPlatformService.addCloudPlatform(
                email,
                GOOGLEDRIVE,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(), null);
    }

    public void unlinkGoogleDrive(String email) {
        cloudPlatformService.deleteCloudPlatform(email, GOOGLEDRIVE);
    }
}