package com.authorisation.services;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.entities.VerificationToken;
import com.authorisation.exception.InvalidPasswordException;
import com.authorisation.exception.UserAlreadyExistsException;
import com.authorisation.exception.UserNotFoundException;
import com.authorisation.mappers.UserMapper;
import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.password.PasswordResetRequest;
import com.authorisation.repositories.VerificationTokenRepository;
import com.authorisation.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserEntityRepository userEntityRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserMapper userMapper;

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

    public UserEntity registerGoogleUser(String email, String firstname, String lastName, String pictureUrl, String refreshToken) {

        Optional<UserEntity> userOptional = this.findUserByEmail(email);

        if (userOptional.isPresent()) {
            return null;
        }

        var newUser = new UserEntity();

        newUser.setGoogleProfileImageUrl(pictureUrl);
        newUser.setEmail(email);
        newUser.setFirstName(firstname);
        newUser.setLastName(lastName);
        newUser.setRole("USER");
        newUser.setEnabled(true);
        newUser.setAccountType("GOOGLE");
        newUser.setRefreshToken(refreshToken);

        return userEntityRepository.save(newUser);
    }

    public UserDto login(CredentialsDto credentialsDto) {
        UserEntity user = userEntityRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Unknown user", HttpStatus.NOT_FOUND));

        if (!user.isEnabled()) {
            throw new UserNotFoundException("User not verified", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new InvalidPasswordException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto googleLogin(String email) {
        UserEntity user = userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Unknown user", HttpStatus.NOT_FOUND));

        return userMapper.toUserDto(user);
    }

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    @Override
    public UserDto findUserByEmailDto(String email) {
        UserEntity userEntity = userEntityRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toUserDto(userEntity);
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
    public void createPasswordResetToken(UserEntity userEntity, String passwordToken, PasswordResetRequest passwordResetRequest) {
        passwordResetTokenService.createPasswordResetTokenForUser(userEntity, passwordToken, passwordResetRequest);
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
