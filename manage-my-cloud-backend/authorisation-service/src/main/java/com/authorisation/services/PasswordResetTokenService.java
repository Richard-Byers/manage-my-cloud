package com.authorisation.services;

import com.authorisation.entities.PasswordResetToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.registration.password.PasswordResetRequest;
import com.authorisation.repositories.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void createPasswordResetTokenForUser(UserEntity userEntity, String passwordToken, PasswordResetRequest passwordResetRequest) {

        PasswordResetToken passwordResetToken = new PasswordResetToken(passwordToken, userEntity, passwordResetRequest.getNewPassword());
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public String validatePasswordResetToken(String verificationToken) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(verificationToken);

        if (token == null) {
            return "Invalid password reset token";
        }

        Calendar calendar = Calendar.getInstance();

        if ((token.getExpiryTime().getTime() - calendar.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(token);
            return "Verification token has expired";
        }

        return "valid";
    }

    public Optional<UserEntity> findUserByPasswordToken(String passwordToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordToken).getUserEntity());
    }


}
