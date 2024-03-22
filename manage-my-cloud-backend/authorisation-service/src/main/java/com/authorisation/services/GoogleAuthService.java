package com.authorisation.services;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.response.GoogleDriveLinkResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

@RequiredArgsConstructor
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
            GoogleIdToken idToken = GoogleIdToken.parse(GsonFactory.getDefaultInstance(), idTokenStr);
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            userService.registerGoogleUser(email, firstName, lastName, pictureUrl);

            UserDto userDto = userService.googleLogin(email);
            userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));

            userService.updateFirstLogin(email);

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
            String scope = getAccessTokenScope(tokenResponse.getAccessToken());
            String driveEmail = driveInformationService.getGoogleDriveEmail(tokenResponse.getRefreshToken(), tokenResponse.getAccessToken());
            boolean gaveGmailPermissions = false;

            if (scope.contains("https://mail.google.com/") || scope.contains("https://www.googleapis.com/auth/gmail.modify")) {
                gaveGmailPermissions = true;
            }

            boolean isDriveLinked = cloudPlatformService.isDriveLinked(email, driveEmail, GOOGLEDRIVE);

            if (isDriveLinked) {
                googleDriveLinkResponse.setError("Drive already linked");
                return googleDriveLinkResponse;
            }

            storeUserPlatformLink(tokenResponse, email, driveEmail, gaveGmailPermissions);

            return googleDriveLinkResponse;
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }

    public GoogleDriveLinkResponse linkGmail(String authCode, String email) {
        String jsonString = authCode.substring(authCode.indexOf("{"));
        JSONObject jsonObject = new JSONObject(jsonString);
        String authCodeOutput = jsonObject.getString("authCode");
        GoogleDriveLinkResponse googleDriveLinkResponse = new GoogleDriveLinkResponse();

        try {
            GoogleTokenResponse tokenResponse = getGoogleTokenResponse(authCodeOutput, googleCredentialsJson);
            String scope = getAccessTokenScope(tokenResponse.getAccessToken());
            String driveEmail = driveInformationService.getGoogleDriveEmail(tokenResponse.getRefreshToken(), tokenResponse.getAccessToken());
            boolean gaveGmailPermissions = false;

            if (scope.contains("https://mail.google.com/") || scope.contains("https://www.googleapis.com/auth/gmail.modify")) {
                gaveGmailPermissions = true;
            }

            UpdateUserPlatformLink(tokenResponse, email, driveEmail, gaveGmailPermissions);

            return googleDriveLinkResponse;
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
        return null;
    }


    public String getAccessTokenScope(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Parse the response body into a JsonNode
            JsonNode jsonNode = mapper.readTree(response.body().string());

            // Return the "scope" value as a String
            return jsonNode.get("scope").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void storeUserPlatformLink(GoogleTokenResponse tokenResponse, String email, String driveEmail, Boolean gaveGmailPermissions) {
        cloudPlatformService.addCloudPlatform(
                email,
                GOOGLEDRIVE,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(), null, driveEmail, gaveGmailPermissions);
    }

    public void UpdateUserPlatformLink(GoogleTokenResponse tokenResponse, String email, String driveEmail, Boolean gaveGmailPermissions) {
        cloudPlatformService.updateCloudPlatform(
                email,
                GOOGLEDRIVE,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(), null, driveEmail, gaveGmailPermissions);
    }

    public void unlinkGoogleDrive(String email, String driveEmail) {
        cloudPlatformService.deleteCloudPlatform(email, GOOGLEDRIVE, driveEmail);
    }
}