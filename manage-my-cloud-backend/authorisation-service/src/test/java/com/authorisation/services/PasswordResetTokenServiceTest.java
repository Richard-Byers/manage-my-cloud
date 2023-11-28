package com.authorisation.services;

import com.authorisation.entities.PasswordResetToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.registration.password.PasswordResetRequest;
import com.authorisation.repositories.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Date;

import static com.authorisation.givens.PasswordResetRequestGivens.generatePasswordResetRequest;
import static com.authorisation.givens.PasswordResetRequestGivens.generatePasswordResetToken;
import static com.authorisation.givens.UserEntityGivens.generateUserEntityEnabled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = PasswordResetTokenService.class)
@ExtendWith(MockitoExtension.class)
@Import(TestServiceConfig.class)
class PasswordResetTokenServiceTest {

    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Test
    void createPasswordResetTokenForUser_createsAndSavesToken() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String passwordToken = "passwordToken";
        PasswordResetRequest passwordResetRequest = generatePasswordResetRequest();

        //when
        passwordResetTokenService.createPasswordResetTokenForUser(userEntity, passwordToken, passwordResetRequest);

        //then
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void validatePasswordResetToken_validatesToken_returnsValid() {
        //given
        String expected = "valid";
        String verificationToken = "verificationToken";
        PasswordResetToken passwordResetToken = generatePasswordResetToken();

        //when
        given(passwordResetTokenRepository.findByToken(verificationToken)).willReturn(passwordResetToken);
        String actual = passwordResetTokenService.validatePasswordResetToken(verificationToken);

        //then
        verify(passwordResetTokenRepository).findByToken(verificationToken);
        assertEquals(expected, actual);
    }

    @Test
    void validatePasswordResetToken_validatesToken_returnsInvalid() {
        //given
        String expected = "Invalid password reset token";
        String verificationToken = "verificationToken";

        //when
        given(passwordResetTokenRepository.findByToken(verificationToken)).willReturn(null);
        String actual = passwordResetTokenService.validatePasswordResetToken(verificationToken);

        //then
        verify(passwordResetTokenRepository).findByToken(verificationToken);
        assertEquals(expected, actual);
    }

    @Test
    void validatePasswordResetToken_validatesToken_returnsExpiredToken() {
        //given
        String expected = "Verification token has expired";
        String verificationToken = "verificationToken";
        PasswordResetToken passwordResetToken = generatePasswordResetToken();
        passwordResetToken.setExpiryTime(new Date());

        //when
        given(passwordResetTokenRepository.findByToken(verificationToken)).willReturn(passwordResetToken);
        String actual = passwordResetTokenService.validatePasswordResetToken(verificationToken);

        //then
        verify(passwordResetTokenRepository).findByToken(verificationToken);
        assertEquals(expected, actual);
    }
}
