package com.authorisation.user;

import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.token.VerificationToken;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<UserEntity> getUsers();

    UserEntity registerUser(RegistrationRequest registrationRequest);

    Optional<UserEntity> findUserByEmail(String email);

    void saveVerificationToken(UserEntity userEntity, String verificationToken);

    String validateToken(String verificationToken);

    VerificationToken generateNewVerificationToken(String oldToken);

    void createPasswordResetToken(UserEntity userEntity, String passwordToken);

    String validatePasswordResetToken(String passwordToken);

    UserEntity findUserByPasswordToken(String passwordResetToken);

    void resetUserPassword(UserEntity userEntity, String newPassword);
}
