package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.response.OneDriveTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mmc.drive.DriveInformationService;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    DriveInformationService driveInformationService;

    @InjectMocks
    private OneDriveService oneDriveService;

    private boolean areOneDriveTokenResponsesEqual(OneDriveTokenResponse expected, OneDriveTokenResponse actual) {
        if (expected == actual) return true;
        if (expected == null || actual == null) return false;
        return Objects.equals(expected.getAccessToken(), actual.getAccessToken()) &&
                Objects.equals(expected.getRefreshToken(), actual.getRefreshToken()) &&
                Objects.equals(expected.getExpiresIn(), actual.getExpiresIn());
    }

    @Test
    void getAndStoreUserTokens_ReturnsOneDriveTokenResponse() {
        String authCode = "auth_code";
        String email = "email@example.com";
        String driveEmail = "emaildrive@example.com";
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
        when(driveInformationService.getOneDriveEmail(any(), any())).thenReturn(driveEmail);

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

    @Test
    void refreshToken_ReturnsOneDriveTokenResponse() {
        String refreshToken = "refresh_token";
        String driveEmail = "emaildrive@example.com";
        String email = "email@example.com";
        ReflectionTestUtils.setField(oneDriveService, "clientId", "testClientId");
        ReflectionTestUtils.setField(oneDriveService, "redirectUri", "testRedirectUri");
        ReflectionTestUtils.setField(oneDriveService, "clientSecret", "testClientSecret");

        OneDriveTokenResponse expectedOneDriveTokenResponse = new OneDriveTokenResponse();
        expectedOneDriveTokenResponse.setAccessToken("access_token");
        expectedOneDriveTokenResponse.setRefreshToken("refresh_token");
        expectedOneDriveTokenResponse.setExpiresIn(3600L);

        CloudPlatform cloudPlatform = new CloudPlatform();
        cloudPlatform.setAccessToken("old_access_token");
        cloudPlatform.setRefreshToken("old_refresh_token");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ArgumentMatchers.<Class<OneDriveTokenResponse>>notNull()))
                .thenReturn(Mono.just(expectedOneDriveTokenResponse));
        when(driveInformationService.getOneDriveEmail(any(), any())).thenReturn(driveEmail);
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);

        OneDriveTokenResponse actualOneDriveTokenResponse = oneDriveService.refreshToken(refreshToken, driveEmail, email);

        assertEquals(expectedOneDriveTokenResponse, actualOneDriveTokenResponse);
        verify(cloudPlatformService, times(1)).saveCloudPlatform(cloudPlatform);
    }

    @Test
    void getAndStoreUserTokens_TokenAboutToExpire_RefreshesToken() {
        String authCode = "auth_code";
        String email = "email@example.com";
        String driveEmail = "emaildrive@example.com";
        ReflectionTestUtils.setField(oneDriveService, "clientId", "testClientId");
        ReflectionTestUtils.setField(oneDriveService, "redirectUri", "testRedirectUri");
        ReflectionTestUtils.setField(oneDriveService, "clientSecret", "testClientSecret");

        OneDriveTokenResponse initialOneDriveTokenResponse = new OneDriveTokenResponse();
        initialOneDriveTokenResponse.setAccessToken("access_token");
        initialOneDriveTokenResponse.setRefreshToken("refresh_token");
        initialOneDriveTokenResponse.setExpiresIn(3600L);

        OneDriveTokenResponse refreshedOneDriveTokenResponse = new OneDriveTokenResponse();
        refreshedOneDriveTokenResponse.setAccessToken("refreshed_access_token");
        refreshedOneDriveTokenResponse.setRefreshToken("refreshed_refresh_token");
        refreshedOneDriveTokenResponse.setExpiresIn(7200L);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ArgumentMatchers.<Class<OneDriveTokenResponse>>notNull()))
                .thenReturn(Mono.just(initialOneDriveTokenResponse), Mono.just(refreshedOneDriveTokenResponse));
        when(driveInformationService.getOneDriveEmail(any(), any())).thenReturn(driveEmail);
        when(cloudPlatformService.isDriveLinked(any(), any(), any())).thenReturn(false);

        OneDriveTokenResponse actualOneDriveTokenResponse = oneDriveService.getAndStoreUserTokens(authCode, email);
        assertFalse(areOneDriveTokenResponsesEqual(refreshedOneDriveTokenResponse, actualOneDriveTokenResponse));
        verify(cloudPlatformService, times(1)).addCloudPlatform(anyString(), anyString(), anyString(), anyString(), any(Date.class), anyString());
    }



}