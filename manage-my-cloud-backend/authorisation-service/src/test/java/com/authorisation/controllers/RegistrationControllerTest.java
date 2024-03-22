package com.authorisation.controllers;

import com.authorisation.entities.PasswordResetToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.entities.VerificationToken;
import com.authorisation.event.RegistrationCompleteEventListener;
import com.authorisation.exception.UserAlreadyExistsException;
import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.password.PasswordResetRequest;
import com.authorisation.repositories.PasswordResetTokenRepository;
import com.authorisation.repositories.VerificationTokenRepository;
import com.authorisation.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.authorisation.givens.PasswordResetRequestGivens.generatePasswordResetRequest;
import static com.authorisation.givens.RegistrationRequestGivens.generateRegistrationRequest;
import static com.authorisation.givens.UserEntityGivens.generateUserEntity;
import static com.authorisation.givens.VerificationTokenGivens.generateDisabledEntityToken;
import static com.authorisation.givens.VerificationTokenGivens.generateEnabledEntityToken;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    private static final String REGISTER_URL = "/register";
    private static final String VERIFY_EMAIL = REGISTER_URL + "/verifyEmail";
    private static final String RESEND_VERIFICATION_EMAIL = REGISTER_URL + "/resendVerificationEmail";
    private static final String RESET_USER_PASSWORD = REGISTER_URL + "/resetUserPassword";
    private static final String RESET_PASSWORD = REGISTER_URL + "/resetPassword";
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private VerificationTokenRepository verificationTokenRepository;
    @MockBean
    private RegistrationCompleteEventListener eventListener;
    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    void registrationController_register_returnsOkWithVerificationEmail() throws Exception {
        // given
        RegistrationRequest registrationRequest = generateRegistrationRequest();
        UserEntity expectedUserEntity = generateUserEntity();
        String expectedMessage = expectedUserEntity.getEmail();

        // when
        given(userService.registerUser(registrationRequest)).willReturn(expectedUserEntity);
        Mockito.doNothing().when(eventPublisher).publishEvent(any());

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(REGISTER_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(registrationRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void registrationController_registerUserAlreadyExists_throwsUserAlreadyExistsException() throws Exception {
        // given
        RegistrationRequest registrationRequest = generateRegistrationRequest();
        UserAlreadyExistsException expectedException = new UserAlreadyExistsException(String.format("User with email %s already exists", registrationRequest.email()));

        // when
        given(userService.registerUser(registrationRequest)).willThrow(expectedException);

        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(REGISTER_URL)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(registrationRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();
        //then
        assertEquals(expectedException.getMessage(), actualMessage);
    }

    @Test
    void registrationController_verifyEmailUserEnabled_returnsAccountAlreadyVerifiedMessage() throws Exception {
        // given
        String tokenRequest = "token";
        VerificationToken expectedVerificationToken = generateEnabledEntityToken();

        // when
        given(verificationTokenRepository.findByToken(tokenRequest)).willReturn(expectedVerificationToken);
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                get(VERIFY_EMAIL)
                                        .param("token", tokenRequest)
                        )
                        // then
                        .andExpect(status().isFound())
                        .andExpect(result -> assertTrue(result.getResponse().getRedirectedUrl().contains("/login?message=already_verified")))
                        .andReturn();
    }

    @Test
    void registrationController_verifyEmailInvalidToken_returnsInvalidLinkMessage() throws Exception {
        // given
        String tokenRequest = "token";
        VerificationToken expectedVerificationToken = generateDisabledEntityToken();

        // when
        given(verificationTokenRepository.findByToken(tokenRequest)).willReturn(expectedVerificationToken);
        given(userService.validateToken(tokenRequest)).willReturn("Invalid verification token");

                mockMvc
                        .perform(
                                get(VERIFY_EMAIL)
                                        .param("token", tokenRequest)
                        )
                        // then
                        .andExpect(status().isFound())
                        .andExpect(result -> assertTrue(result.getResponse().getRedirectedUrl().contains("/message?message=The link is invalid or broken, <a href=\"http://localhost:80/register/resendVerificationEmail?token=token\">Click Here</a> to resend verification email")));

    }

    @Test
    void registrationController_verifyEmailValid_returnsOkWithMessage() throws Exception {
        // given
        String tokenRequest = "token";
        VerificationToken expectedVerificationToken = generateDisabledEntityToken();

        // when
        given(verificationTokenRepository.findByToken(tokenRequest)).willReturn(expectedVerificationToken);
        given(userService.validateToken(tokenRequest)).willReturn("valid");

                mockMvc
                        .perform(
                                get(VERIFY_EMAIL)
                                        .param("token", tokenRequest)
                        )
                        // then
                        .andExpect(status().isFound())
                        .andExpect(result -> assertTrue(result.getResponse().getRedirectedUrl().contains("/login?message=verification_success")));

    }

    @Test
    void registrationController_resendVerificationEmail_returnsOkWithMessage() throws Exception {
        // given
        String tokenRequest = "token";
        VerificationToken expectedVerificationToken = generateDisabledEntityToken();
        String expectedMessage = String.format("A new verification email has been sent to %s. Please verify your account", expectedVerificationToken.getUserEntity().getEmail());

        // when
        given(userService.generateNewVerificationToken(tokenRequest)).willReturn(expectedVerificationToken);
        Mockito.doNothing().when(eventListener).sendVerificationEmail(any(String.class), any(UserEntity.class));
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                get(RESEND_VERIFICATION_EMAIL)
                                        .contentType("application/json").queryParam("token", tokenRequest))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
        verify(eventListener).sendVerificationEmail(any(String.class), any(UserEntity.class));
    }

    @Test
    void registrationController_resendVerificationEmail_throwsException() throws Exception {
        // given
        String tokenRequest = "token";
        VerificationToken expectedVerificationToken = generateDisabledEntityToken();
        String expectedMessage = "Error occurred while sending verification email to user: johndoe@gmail.com";

        // when
        given(userService.generateNewVerificationToken(tokenRequest)).willReturn(expectedVerificationToken);
        Mockito.doThrow(new MessagingException()).when(eventListener).sendVerificationEmail(any(String.class), any(UserEntity.class));
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                get(RESEND_VERIFICATION_EMAIL)
                                        .contentType("application/json").queryParam("token", tokenRequest))
                        // then
                        .andExpect(status().isInternalServerError())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void registrationController_resetUserPassword_returnsOkWithResetLink() throws Exception {
        // given
        PasswordResetRequest passwordResetRequest = generatePasswordResetRequest();
        Optional<UserEntity> expectedUserEntity = Optional.of(generateUserEntity());
        String expectedMessage = expectedUserEntity.get().getEmail();

        // when
        given(userService.findUserByEmail(passwordResetRequest.getEmail())).willReturn(expectedUserEntity);
        Mockito.doNothing().when(userService).createPasswordResetToken(any(UserEntity.class), any(String.class), any(PasswordResetRequest.class));
        Mockito.doNothing().when(eventListener).sendPasswordResetVerificationEmail(any(String.class), any(UserEntity.class));
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(RESET_USER_PASSWORD)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(passwordResetRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void registrationController_resetUserPasswordUserNotFound_returnsErrorMessage() throws Exception {
        // given
        PasswordResetRequest passwordResetRequest = generatePasswordResetRequest();
        Optional<UserEntity> expectedUserEntity = Optional.empty();
        String expectedMessage = "User is not registered";

        // when
        given(userService.findUserByEmail(passwordResetRequest.getEmail())).willReturn(expectedUserEntity);
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                post(RESET_USER_PASSWORD)
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(passwordResetRequest)))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void registrationController_resetPassword_returnsOkWithMessage() throws Exception {
        // given
        String token = "token";
        UserEntity expectedUserEntity = generateUserEntity();
        String expectedMessage = "Password reset successfully";
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        // when
        given(userService.validatePasswordResetToken(token)).willReturn("valid");
        given(userService.findUserByPasswordToken(token)).willReturn(expectedUserEntity);
        given(passwordResetTokenRepository.findByToken(token)).willReturn(passwordResetToken);
        Mockito.doNothing().when(userService).resetUserPassword(expectedUserEntity, passwordResetToken.getNewPassword());
        Mockito.doNothing().when(passwordResetTokenRepository).delete(passwordResetToken);
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                get(RESET_PASSWORD).param(token, token))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void registrationController_resetPasswordNullEntity_returnsErrorMessage() throws Exception {
        // given
        String token = "token";
        String expectedMessage = "Invalid password reset token";
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        // when
        given(userService.validatePasswordResetToken(token)).willReturn("valid");
        given(userService.findUserByPasswordToken(token)).willReturn(null);
        given(passwordResetTokenRepository.findByToken(token)).willReturn(passwordResetToken);
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                get(RESET_PASSWORD).param(token, token))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void registrationController_resetPasswordInvalidPasswordResetToken_returnsErrorMessage() throws Exception {
        // given
        String token = "token";
        String expectedMessage = "Invalid password reset token";
        // when
        given(userService.validatePasswordResetToken(token)).willReturn("invalid");
        MvcResult mvcResult =
                mockMvc
                        .perform(
                                get(RESET_PASSWORD).param(token, token))
                        // then
                        .andExpect(status().isOk())
                        .andReturn();
        String actualMessage = mvcResult.getResponse().getContentAsString();

        //then
        assertEquals(expectedMessage, actualMessage);
    }


}
