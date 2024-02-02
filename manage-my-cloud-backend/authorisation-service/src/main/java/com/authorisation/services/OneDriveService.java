package com.authorisation.services;

import com.authorisation.response.OneDriveTokenResponse;
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

    @Autowired
    public OneDriveService(@Value("${onedrive.clientId}") String clientId,
                           @Value("${onedrive.redirectUri}") String redirectUri,
                           @Value("${onedrive.clientSecret}") String clientSecret,
                           CloudPlatformService cloudPlatformService,
                           WebClient webClient) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.cloudPlatformService = cloudPlatformService;
        this.webClient = webClient;
    }

    public OneDriveTokenResponse getAndStoreUserTokens(String authCode, String email) {
        try {
            String scope = "files.readwrite.all offline_access";

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
            storeUserPlatformLink(oneDriveTokenResponse, email, accessExpiryDate);

            return oneDriveTokenResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unlinkOneDrive(String email) {
        cloudPlatformService.deleteCloudPlatform(email, ONEDRIVE);
    }

    private void storeUserPlatformLink(OneDriveTokenResponse oneDriveTokenResponse, String email, Date accessExpiryDate) {
        cloudPlatformService.addCloudPlatform(
                email,
                ONEDRIVE,
                oneDriveTokenResponse.getAccessToken(),
                oneDriveTokenResponse.getRefreshToken(),
                accessExpiryDate);

    }


}
