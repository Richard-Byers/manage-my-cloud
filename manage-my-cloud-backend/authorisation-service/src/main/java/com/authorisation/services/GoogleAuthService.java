package com.authorisation.services;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.response.GoogleDriveLinkResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.mmc.drive.DriveInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;

import static com.authorisation.Constants.GOOGLEDRIVE;
import static com.authorisation.Constants.ONEDRIVE;
import static com.authorisation.services.GoogleTokenService.getGoogleTokenResponse;

@Service
public class GoogleAuthService {

    private CloudPlatformService cloudPlatformService;
    private UserService userService;
    private UserAuthenticationProvider userAuthenticationProvider;
    private String googleCredentialsJson;
    private DriveInformationService driveInformationService;

    @Autowired
    public GoogleAuthService(@Value("${google.credentials}") String googleCredentialsJson,
                             CloudPlatformService cloudPlatformService,
                             UserService userService,
                             UserAuthenticationProvider userAuthenticationProvider,
                             DriveInformationService driveInformationService) {
        this.googleCredentialsJson = googleCredentialsJson;
        this.cloudPlatformService = cloudPlatformService;
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.driveInformationService = driveInformationService;
    }

    public ResponseEntity<UserDto> storeAuthCode(String authCode) {
        String jsonString = authCode.substring(authCode.indexOf("{"));
        JSONObject jsonObject = new JSONObject(jsonString);
        String authCodeOutput = jsonObject.getString("authCode");

        try {
            GoogleTokenResponse tokenResponse = getGoogleTokenResponse(authCodeOutput, googleCredentialsJson);

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

    public GoogleDriveLinkResponse linkGoogleAccount(String authCode, String email) {
        String jsonString = authCode.substring(authCode.indexOf("{"));
        JSONObject jsonObject = new JSONObject(jsonString);
        String authCodeOutput = jsonObject.getString("authCode");
        GoogleDriveLinkResponse googleDriveLinkResponse = new GoogleDriveLinkResponse();

        try {
            GoogleTokenResponse tokenResponse = getGoogleTokenResponse(authCodeOutput, googleCredentialsJson);
            String driveEmail = driveInformationService.getGoogleDriveEmail(tokenResponse.getRefreshToken(), tokenResponse.getAccessToken());

            boolean isDriveLinked = cloudPlatformService.isDriveLinked(email, driveEmail, GOOGLEDRIVE);

            if (isDriveLinked) {
                googleDriveLinkResponse.setError("Drive already linked");
                return googleDriveLinkResponse;
            }

            storeUserPlatformLink(tokenResponse, email, driveEmail);

            return googleDriveLinkResponse;
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }

    public void storeUserPlatformLink(GoogleTokenResponse tokenResponse, String email, String driveEmail) {
        cloudPlatformService.addCloudPlatform(
                email,
                GOOGLEDRIVE,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(), null, driveEmail);
    }

    public void unlinkGoogleDrive(String email, String driveEmail) {
        cloudPlatformService.deleteCloudPlatform(email, GOOGLEDRIVE, driveEmail);
    }
}