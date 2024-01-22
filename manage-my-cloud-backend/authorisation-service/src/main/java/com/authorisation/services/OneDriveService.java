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

import static com.authorisation.Constants.MS_AUTH_CODE_URL;

@AllArgsConstructor
@Service
public class OneDriveService {

    private RestTemplate restTemplate;
    private final CloudPlatformService cloudPlatformService;

    public OneDriveTokenResponse getAndStoreUserTokens(String authCode, String email) {
        try {
            String clientId = "a1418afc-b4bb-4e55-8184-cc77c502f087";
            String redirectUri = "http://localhost:3000/manage-connections";
            String scope = "files.readwrite.all offline_access";
            String clientSecret = "kqm8Q~.rVliZ68SAuH7VWl34GLYOi26e_UH0ccqU";
            String grantType = "authorization_code";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("redirect_uri", redirectUri);
            map.add("scope", scope);
            map.add("client_secret", clientSecret);
            map.add("grant_type", grantType);
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

    private void storeUserPlatformLink(OneDriveTokenResponse oneDriveTokenResponse, String email) {
        cloudPlatformService.addCloudPlatform(
                email,
                "OneDrive",
                oneDriveTokenResponse.getAccessToken(),
                oneDriveTokenResponse.getRefreshToken());

    }


}
