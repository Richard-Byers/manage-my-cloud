package com.authorisation.controllers;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.EmailDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.RefreshToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.exception.InvalidPasswordException;
import com.authorisation.exception.UserNotFoundException;
import com.authorisation.requests.RefreshTokenRequest;
import com.authorisation.response.JwtResponse;
import com.authorisation.services.RefreshTokenService;
import com.authorisation.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Optional;

import static com.authorisation.givens.CredentialsGivens.generateCredentialsDto;
import static com.authorisation.givens.CredentialsGivens.generateEmailDto;
import static com.authorisation.givens.UserDtoGivens.generateUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String LOGIN_URL = "/login";
    private static final String REFRESH_USER_URL = "/refresh-user";
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserAuthenticationProvider userAuthenticationProvider;
    @MockBean
    private RefreshTokenService refreshTokenService;

    @Test
    void authController_login_returnsOkWithUser() throws Exception {
        // given
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();
        UserDto expected = generateUserDto();
        RefreshToken mockRefreshToken = new RefreshToken();

        // when
        given(userService.login(credentialsDtoRequest)).willReturn(expected);
        given(userAuthenticationProvider.createToken(expected.getEmail())).willReturn(expected.getToken());
        given(refreshTokenService.createRefreshtoken(anyString())).willReturn(mockRefreshToken);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(LOGIN_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(credentialsDtoRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        UserDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        //then
        assertEquals(expected, result);
    }

    @Test
    void authController_loginThrowsUserNotFoundException_returnsExceptionWith400Status() throws Exception {
        // given
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();
        UserNotFoundException expectedUserNotFoundException = new UserNotFoundException("User not found", HttpStatus.BAD_REQUEST);

        // when
        given(userService.login(credentialsDtoRequest)).willThrow(expectedUserNotFoundException);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(LOGIN_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(credentialsDtoRequest)))
                        // then
                        .andExpect(status().isBadRequest())
                        .andReturn();
        UserNotFoundException result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserNotFoundException.class);

        //then
        assertEquals(expectedUserNotFoundException.getMessage(), result.getMessage());
    }

    @Test
    void authController_loginThrowsInvalidPasswordException_returnsExceptionWith400Status() throws Exception {
        // given
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();
        InvalidPasswordException expectedInvalidPasswordException = new InvalidPasswordException("Invalid password", HttpStatus.BAD_REQUEST);

        // when
        given(userService.login(credentialsDtoRequest)).willThrow(expectedInvalidPasswordException);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(LOGIN_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(credentialsDtoRequest)))
                        // then
                        .andExpect(status().isBadRequest())
                        .andReturn();
        UserNotFoundException result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserNotFoundException.class);

        //then
        assertEquals(expectedInvalidPasswordException.getMessage(), result.getMessage());
    }

    @Test
    void authController_refreshUser_returnsOkWithUser() throws Exception {
        // given
        EmailDto emailDtoRequest = generateEmailDto();
        UserDto expected = generateUserDto();
        RefreshToken mockRefreshToken = new RefreshToken();

        // when
        given(userService.refreshUser(emailDtoRequest)).willReturn(expected);
        given(userAuthenticationProvider.createToken(expected.getEmail())).willReturn(expected.getToken());
        given(refreshTokenService.findByUserEntityEmail(expected.getEmail())).willReturn(mockRefreshToken);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(REFRESH_USER_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(emailDtoRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        UserDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        //then
        assertEquals(expected, result);
    }

    @Test
    void authController_refreshToken_returnsOkWithJwtResponse() throws Exception {
        // given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("validRefreshToken");
        RefreshToken mockRefreshToken = new RefreshToken();
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        // Create a UserEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        mockRefreshToken.setUserEntity(userEntity);

        // Create a RefreshToken
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(1L); // Set the ID
        refreshToken.setToken("newRefreshToken"); // Set the token
        refreshToken.setExpiryDate(Instant.now().plusMillis(3600000)); // Set the expiry date to 1 hour from now
        refreshToken.setUserEntity(userEntity);

        // when
        given(refreshTokenService.findByToken(refreshTokenRequest.getToken())).willReturn(Optional.of(mockRefreshToken));
        given(refreshTokenService.verifyExpiration(mockRefreshToken)).willReturn(mockRefreshToken);
        given(refreshTokenService.createRefreshtoken(mockRefreshToken.getUserEntity().getEmail())).willReturn(refreshToken);
        given(userAuthenticationProvider.createToken(mockRefreshToken.getUserEntity().getEmail())).willReturn(newAccessToken);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post("/refresh-token")
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        JwtResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JwtResponse.class);

        //then
        assertEquals(newAccessToken, result.getAccessToken());
        assertEquals(newRefreshToken, result.getRefreshToken());
    }

    @Test
    void authController_refreshUserThrowsUserNotFoundException_returnsExceptionWith400Status() throws Exception {
        // given
        EmailDto emailDtoRequest = generateEmailDto();
        UserNotFoundException expectedUserNotFoundException = new UserNotFoundException("User not found", HttpStatus.BAD_REQUEST);

        // when
        given(userService.refreshUser(emailDtoRequest)).willThrow(expectedUserNotFoundException);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(REFRESH_USER_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(emailDtoRequest)))
                        // then
                        .andExpect(status().isBadRequest())
                        .andReturn();
        UserNotFoundException result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserNotFoundException.class);

        //then
        assertEquals(expectedUserNotFoundException.getMessage(), result.getMessage());
    }

}
