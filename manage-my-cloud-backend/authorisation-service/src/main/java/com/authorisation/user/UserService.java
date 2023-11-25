package com.authorisation.user;

import com.authorisation.exception.UserAlreadyExistsException;
import com.authorisation.exception.UserNotFoundException;
import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.password.PasswordResetTokenService;
import com.authorisation.registration.token.VerificationToken;
import com.authorisation.registration.token.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserEntityRepository userEntityRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public List<UserEntity> getUsers() {
        return userEntityRepository.findAll();
    }

    @Override
    public UserEntity registerUser(RegistrationRequest registrationRequest) {

        Optional<UserEntity> userOptional = this.findUserByEmail(registrationRequest.email());

        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", registrationRequest.email()));
        }

        var newUser = new UserEntity();

        newUser.setFirstName(registrationRequest.firstName());
        newUser.setLastName(registrationRequest.lastName());
        newUser.setEmail(registrationRequest.email());
        newUser.setPassword(passwordEncoder.encode(registrationRequest.password()));
        newUser.setRole(registrationRequest.role());

        return userEntityRepository.save(newUser);
    }

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Override
    public void saveVerificationToken(UserEntity userEntity, String verificationToken) {

        var verificationTokenEntity = new VerificationToken(verificationToken, userEntity);
        verificationTokenRepository.save(verificationTokenEntity);
    }

    @Override
    public String validateToken(String verificationToken) {
        VerificationToken token = verificationTokenRepository.findByToken(verificationToken);

        if (token == null) {
            return "Invalid verification token";
        }

        UserEntity userEntity = token.getUserEntity();
        Calendar calendar = Calendar.getInstance();

        if ((token.getExpiryTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(token);
            return "Verification token has expired";
        }

        //set user as verified
        userEntity.setEnabled(true);
        userEntityRepository.save(userEntity);
        //remove stale token
        verificationTokenRepository.delete(token);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        VerificationToken verificationTokenTime = new VerificationToken();

        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryTime(verificationTokenTime.getExpiryTime());

        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public void createPasswordResetToken(UserEntity userEntity, String passwordToken) {
        passwordResetTokenService.createPasswordResetTokenForUser(userEntity, passwordToken);
    }

    @Override
    public String validatePasswordResetToken(String passwordToken) {
        return passwordResetTokenService.validatePasswordResetToken(passwordToken);
    }

    @Override
    public UserEntity findUserByPasswordToken(String passwordResetToken) {
        Optional<UserEntity> userEntity = passwordResetTokenService.findUserByPasswordToken(passwordResetToken);

        if (userEntity.isPresent()) {
            return userEntity.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public void resetUserPassword(UserEntity userEntity, String newPassword) {
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntityRepository.save(userEntity);
    }
}
