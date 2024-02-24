package com.authorisation.services;

import com.authorisation.response.OneDriveTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneDriveServiceTest {

    @Mock
    WebClient webClient;

    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    WebClient.RequestBodySpec requestBodySpec;

    @Mock
    WebClient.ResponseSpec responseSpec;

    @Mock
    private CloudPlatformService cloudPlatformService;

    @InjectMocks
    private OneDriveService oneDriveService;

    @Test
    void getAndStoreUserTokens_ReturnsOneDriveTokenResponse() {
        String authCode = "auth_code";
        String email = "email@example.com";
        ReflectionTestUtils.setField(oneDriveService, "clientId", "testClientId");
        ReflectionTestUtils.setField(oneDriveService, "redirectUri", "testRedirectUri");
        ReflectionTestUtils.setField(oneDriveService, "clientSecret", "testClientSecret");

        OneDriveTokenResponse expectedOneDriveTokenResponse = new OneDriveTokenResponse();
        expectedOneDriveTokenResponse.setAccessToken("access_token");
        expectedOneDriveTokenResponse.setRefreshToken("refresh_token");
        expectedOneDriveTokenResponse.setExpiresIn(3600L);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ArgumentMatchers.<Class<OneDriveTokenResponse>>notNull()))
                .thenReturn(Mono.just(expectedOneDriveTokenResponse));

        OneDriveTokenResponse actualOneDriveTokenResponse = oneDriveService.getAndStoreUserTokens(authCode, email);

        assertEquals(expectedOneDriveTokenResponse, actualOneDriveTokenResponse);
        verify(cloudPlatformService, times(1)).addCloudPlatform(anyString(), anyString(), anyString(), anyString(), any(Date.class), anyString());
    }

    @Test
    void unlinkOneDriveTest_UnlinksDrive() {
        String email = "email@example.com";
        String driveEmail = "email2@example.com";

        oneDriveService.unlinkOneDrive(email, driveEmail);

        verify(cloudPlatformService, times(1)).deleteCloudPlatform(email, "OneDrive", driveEmail);
    }
}