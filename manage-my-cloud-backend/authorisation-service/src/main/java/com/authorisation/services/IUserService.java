package com.authorisation.services;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.entities.VerificationToken;
import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.password.PasswordResetRequest;

import java.util.Optional;

public interface IUserService {

    UserEntity registerUser(RegistrationRequest registrationRequest);

    Optional<UserEntity> findUserByEmail(String email);

    UserDto findUserByEmailDto(String email);

    void saveVerificationToken(UserEntity userEntity, String verificationToken);

    String validateToken(String verificationToken);

    VerificationToken generateNewVerificationToken(String oldToken);

    void createPasswordResetToken(UserEntity userEntity, String passwordToken, PasswordResetRequest passwordResetRequest);

    String validatePasswordResetToken(String passwordToken);

    UserEntity findUserByPasswordToken(String passwordResetToken);

    void resetUserPassword(UserEntity userEntity, String newPassword);

    UserDto login(CredentialsDto credentialsDto);
}
