package com.authorisation.services;

import com.authorisation.response.OneDriveTokenResponse;
import org.mmc.drive.DriveInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

import static com.authorisation.Constants.*;

@Service
public class OneDriveService implements IOneDriveService {

    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;
    private final CloudPlatformService cloudPlatformService;
    private final WebClient webClient;
    private final DriveInformationService driveInformationService;

    @Autowired
    public OneDriveService(@Value("${onedrive.clientId}") String clientId,
                           @Value("${onedrive.redirectUri}") String redirectUri,
                           @Value("${onedrive.clientSecret}") String clientSecret,
                           CloudPlatformService cloudPlatformService,
                           WebClient webClient,
                           DriveInformationService driveInformationService) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.cloudPlatformService = cloudPlatformService;
        this.webClient = webClient;
        this.driveInformationService = driveInformationService;
    }

    public OneDriveTokenResponse getAndStoreUserTokens(String authCode, String email) {
        try {
            String scope = "user.read files.readwrite.all offline_access";

            OneDriveTokenResponse oneDriveTokenResponse = webClient.post()
                    .uri(MS_AUTH_CODE_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("redirect_uri", redirectUri)
                            .with("scope", scope)
                            .with("client_secret", clientSecret)
                            .with("grant_type", ONEDRIVE_GRANT_TYPE)
                            .with("code", authCode))
                    .retrieve()
                    .bodyToMono(OneDriveTokenResponse.class)
                    .block();

            if (oneDriveTokenResponse == null) {
                throw new Exception("OneDrive token response is null");
            }

            Date accessExpiryDate = new Date(System.currentTimeMillis() + (oneDriveTokenResponse.getExpiresIn() * 1000));
            String driveEmail = driveInformationService.getOneDriveEmail(oneDriveTokenResponse.getAccessToken(), accessExpiryDate);
            boolean isDriveLinked = cloudPlatformService.isDriveLinked(email, driveEmail, ONEDRIVE);

            if (isDriveLinked) {
                oneDriveTokenResponse.setError("Drive already linked");
                return oneDriveTokenResponse;
            }

            storeUserPlatformLink(oneDriveTokenResponse, email, accessExpiryDate, driveEmail);

            return oneDriveTokenResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unlinkOneDrive(String email, String driveEmail) {
        cloudPlatformService.deleteCloudPlatform(email, ONEDRIVE, driveEmail);
    }

    private void storeUserPlatformLink(OneDriveTokenResponse oneDriveTokenResponse, String email, Date accessExpiryDate, String driveEmail) {
        cloudPlatformService.addCloudPlatform(
                email,
                ONEDRIVE,
                oneDriveTokenResponse.getAccessToken(),
                oneDriveTokenResponse.getRefreshToken(),
                accessExpiryDate,
                driveEmail,
                false);

    }


}
