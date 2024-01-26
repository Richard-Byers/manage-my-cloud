package com.authorisation.services;

import com.authorisation.response.OneDriveTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneDriveServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CloudPlatformService cloudPlatformService;

    @InjectMocks
    private OneDriveService oneDriveService;

    @Test
    void getAndStoreUserTokens_ReturnsOneDriveTokenResponse() {
        String authCode = "auth_code";
        String email = "email@example.com";

        OneDriveTokenResponse expectedOneDriveTokenResponse = new OneDriveTokenResponse();
        expectedOneDriveTokenResponse.setAccessToken("access_token");
        expectedOneDriveTokenResponse.setRefreshToken("refresh_token");

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(ResponseEntity.of(java.util.Optional.of(expectedOneDriveTokenResponse)));

        OneDriveTokenResponse actualOneDriveTokenResponse = oneDriveService.getAndStoreUserTokens(authCode, email);

        assertEquals(expectedOneDriveTokenResponse, actualOneDriveTokenResponse);
        verify(cloudPlatformService, times(1)).addCloudPlatform(email, "OneDrive", expectedOneDriveTokenResponse.getAccessToken(), expectedOneDriveTokenResponse.getRefreshToken());
    }

    @Test
    void unlinkOneDriveTest_UnlinksDrive() {
        String email = "email@example.com";

        oneDriveService.unlinkOneDrive(email);

        verify(cloudPlatformService, times(1)).deleteCloudPlatform(email, "OneDrive");
    }
}