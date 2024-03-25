package com.authorisation.services;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.RefreshToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.mappers.UserMapper;
import com.authorisation.mappers.UserMapperImpl;
import com.authorisation.response.GoogleDriveLinkResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mmc.drive.DriveInformationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static com.authorisation.TestConstants.GOOGLEDRIVE;
import static com.authorisation.TestConstants.TESTER_EMAIL;
import static com.authorisation.givens.UserEntityGivens.generateUserEntityTesterEmail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {
    @Mock
    private CloudPlatformService cloudPlatformService;
    @Mock
    private UserService userService;
    @Mock
    private UserAuthenticationProvider userAuthenticationProvider;
    @Mock
    private DriveInformationService driveInformationService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private GoogleAuthService googleAuthService;
    UserMapper userMapper = new UserMapperImpl();

    @Test
    void storeAuthCode_ReturnsUserDto() {
        //given
        ReflectionTestUtils.setField(googleAuthService, "googleCredentialsJson", "credentials");
        UserEntity userEntity = generateUserEntityTesterEmail();
        UserDto userDto = userMapper.toUserDto(userEntity);
        String authCode = "{authCode: \"auth_code\"}";
        String idTokenString = "idToken";
        GoogleTokenResponse mockResponse = mock(GoogleTokenResponse.class);
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        RefreshToken refreshToken = new RefreshToken();

        MockedStatic<GoogleTokenService> googleTokenServiceMockedStatic = Mockito.mockStatic(GoogleTokenService.class);
        MockedStatic<GoogleIdToken> googleIdTokenMockedStatic = Mockito.mockStatic(GoogleIdToken.class);

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail(TESTER_EMAIL);
        payload.set("given_name", "test");
        payload.set("family_name", "test");
        payload.set("picture", "picture");

        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String pictureUrl = (String) payload.get("picture");

        try (googleTokenServiceMockedStatic; googleIdTokenMockedStatic) {
            //when
            googleTokenServiceMockedStatic.when(() -> GoogleTokenService.getGoogleTokenResponse(any(), any())).thenReturn(mockResponse);
            googleTokenServiceMockedStatic.when(() -> GoogleIdToken.parse(any(), any())).thenReturn(mockIdToken);
            when(mockResponse.getIdToken()).thenReturn(idTokenString);
            when(mockIdToken.getPayload()).thenReturn(payload);

            when(userService.registerGoogleUser(email, firstName, lastName, pictureUrl)).thenReturn(userEntity);
            when(userService.googleLogin(email)).thenReturn(userDto);
            when(refreshTokenService.createRefreshtoken(email)).thenReturn(refreshToken);
            ResponseEntity<UserDto> userDtoResponseEntity = googleAuthService.storeAuthCode(authCode);

            //then
            assertEquals(userDto.getEmail(), userDtoResponseEntity.getBody().getEmail());
            assertEquals(userDto.getFirstName(), userDtoResponseEntity.getBody().getFirstName());
        }
    }

    @Test
    void linkGoogleAccount_DriveNotLinked_ReturnsGoogleDriveLinkResponse() throws IOException {
        //given
        ReflectionTestUtils.setField(googleAuthService, "googleCredentialsJson", "credentials");
        UserEntity userEntity = generateUserEntityTesterEmail();
        String authCode = "{authCode: \"auth_code\"}";
        GoogleTokenResponse mockResponse = mock(GoogleTokenResponse.class);
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        MockedStatic<GoogleTokenService> googleTokenServiceMockedStatic = Mockito.mockStatic(GoogleTokenService.class);

        GoogleAuthService googleAuthServiceSpy = Mockito.spy(googleAuthService);

        try (googleTokenServiceMockedStatic) {
            //when
            googleTokenServiceMockedStatic.when(() -> GoogleTokenService.getGoogleTokenResponse(any(), any())).thenReturn(mockResponse);
            when(mockResponse.getRefreshToken()).thenReturn(refreshToken);
            when(mockResponse.getAccessToken()).thenReturn(accessToken);
            when(driveInformationService.getGoogleDriveEmail(refreshToken, accessToken)).thenReturn(TESTER_EMAIL);
            when(cloudPlatformService.isDriveLinked(userEntity.getEmail(), TESTER_EMAIL, GOOGLEDRIVE)).thenReturn(false);
            when(cloudPlatformService.addCloudPlatform(userEntity.getEmail(), GOOGLEDRIVE, accessToken, refreshToken, null, TESTER_EMAIL, true)).thenReturn(
                    null);

            doReturn("https://www.googleapis.com/auth/drive https://mail.google.com/").when(googleAuthServiceSpy).getAccessTokenScope(any());

            GoogleDriveLinkResponse userDtoResponseEntity = googleAuthServiceSpy.linkGoogleAccount(authCode, userEntity.getEmail());

            //then
            assertNull(userDtoResponseEntity.getError());
        }
    }

    @Test
    void linkGoogleAccount_DriveAlreadyLinked_ReturnsGoogleDriveLinkResponseWithError() throws IOException {
        //given
        ReflectionTestUtils.setField(googleAuthService, "googleCredentialsJson", "credentials");
        UserEntity userEntity = generateUserEntityTesterEmail();
        String authCode = "{authCode: \"auth_code\"}";
        GoogleTokenResponse mockResponse = mock(GoogleTokenResponse.class);
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        MockedStatic<GoogleTokenService> googleTokenServiceMockedStatic = Mockito.mockStatic(GoogleTokenService.class);

        GoogleAuthService googleAuthServiceSpy = Mockito.spy(googleAuthService);

        try (googleTokenServiceMockedStatic) {
            //when
            googleTokenServiceMockedStatic.when(() -> GoogleTokenService.getGoogleTokenResponse(any(), any())).thenReturn(mockResponse);
            when(mockResponse.getRefreshToken()).thenReturn(refreshToken);
            when(mockResponse.getAccessToken()).thenReturn(accessToken);
            when(driveInformationService.getGoogleDriveEmail(refreshToken, accessToken)).thenReturn(TESTER_EMAIL);
            when(cloudPlatformService.isDriveLinked(userEntity.getEmail(), TESTER_EMAIL, GOOGLEDRIVE)).thenReturn(true);

            // Mock the getAccessTokenScope method
            doReturn("https://www.googleapis.com/auth/drive https://mail.google.com/").when(googleAuthServiceSpy).getAccessTokenScope(any());

            GoogleDriveLinkResponse userDtoResponseEntity = googleAuthServiceSpy.linkGoogleAccount(authCode, userEntity.getEmail());

            //then
            assertEquals("Drive already linked", userDtoResponseEntity.getError());
        }
    }

    @Test
    void unlinkGoogleDrive_UnlinksAccount() {
        //given
        UserEntity userEntity = generateUserEntityTesterEmail();
        String driveEmail = "driveEmail";

        //when
        googleAuthService.unlinkGoogleDrive(userEntity.getEmail(), driveEmail);

        //then
        verify(cloudPlatformService, times(1)).deleteCloudPlatform(userEntity.getEmail(), GOOGLEDRIVE, driveEmail);
    }
}
