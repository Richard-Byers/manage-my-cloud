package com.authorisation.services;

import com.authorisation.response.OneDriveTokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.authorisation.Constants.*;

@AllArgsConstructor
@Service
public class OneDriveService implements IOneDriveService {

    private RestTemplate restTemplate;
    private final CloudPlatformService cloudPlatformService;

    public OneDriveTokenResponse getAndStoreUserTokens(String authCode, String email) {
        try {
            String clientId = System.getenv(ONEDRIVE_CLIENT_ID);
            String redirectUri = System.getenv(ONEDRIVE_REDIRECT_URI);
            String scope = "files.readwrite.all offline_access";
            String clientSecret = System.getenv(ONEDRIVE_CLIENT_SECRET);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("redirect_uri", redirectUri);
            map.add("scope", scope);
            map.add("client_secret", clientSecret);
            map.add("grant_type", ONEDRIVE_GRANT_TYPE);
            map.add("code", authCode);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            OneDriveTokenResponse oneDriveTokenResponse = restTemplate.exchange(
                    MS_AUTH_CODE_URL,
                    HttpMethod.POST,
                    entity,
                    OneDriveTokenResponse.class
            ).getBody();

            storeUserPlatformLink(oneDriveTokenResponse, email);

            return oneDriveTokenResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unlinkOneDrive(String email) {
        cloudPlatformService.deleteCloudPlatform(email, ONEDRIVE);
    }

    private void storeUserPlatformLink(OneDriveTokenResponse oneDriveTokenResponse, String email) {
        cloudPlatformService.addCloudPlatform(
                email,
                ONEDRIVE,
                oneDriveTokenResponse.getAccessToken(),
                oneDriveTokenResponse.getRefreshToken());

    }


}
