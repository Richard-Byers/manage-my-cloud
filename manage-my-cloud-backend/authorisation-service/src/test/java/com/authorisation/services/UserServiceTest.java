package com.authorisation.services;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.entities.VerificationToken;
import com.authorisation.exception.InvalidPasswordException;
import com.authorisation.exception.UserAlreadyExistsException;
import com.authorisation.exception.UserNotFoundException;
import com.authorisation.exception.UserNotVerifiedException;
import com.authorisation.mappers.UserMapper;
import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.password.PasswordResetRequest;
import com.authorisation.repositories.UserEntityRepository;
import com.authorisation.repositories.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static com.authorisation.givens.CredentialsGivens.generateCredentialsDto;
import static com.authorisation.givens.RegistrationRequestGivens.generateRegistrationRequest;
import static com.authorisation.givens.UserEntityGivens.generateUserEntity;
import static com.authorisation.givens.UserEntityGivens.generateUserEntityEnabled;
import static com.authorisation.givens.VerificationTokenGivens.generateDisabledEntityToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest(classes = UserService.class)
@ExtendWith(MockitoExtension.class)
@Import(TestServiceConfig.class)
class UserServiceTest {

    @MockBean
    private UserEntityRepository userEntityRepository;
    @MockBean
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private PasswordResetTokenService passwordResetTokenService;
    @MockBean
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    void registerUser_userDoesntAlreadyExist_returnsNewUserEntity() {
        //given
        UserEntity expecteduserEntity = generateUserEntity();
        RegistrationRequest registrationRequest = generateRegistrationRequest();

        //when
        given(userEntityRepository.findByEmail(registrationRequest.email())).willReturn(Optional.empty());
        given(userEntityRepository.save(any(UserEntity.class))).willReturn(expecteduserEntity);

        UserEntity actualResult = userService.registerUser(registrationRequest);
        //then
        assertEquals(expecteduserEntity, actualResult);
    }

    @Test
    void registerUser_userExists_throwsUserAlreadyExistsException() {
        //given
        UserEntity expecteduserEntity = generateUserEntity();
        RegistrationRequest registrationRequest = generateRegistrationRequest();
        UserAlreadyExistsException expected = new UserAlreadyExistsException(String.format("User with email %s already exists", registrationRequest.email()));

        //when
        given(userEntityRepository.findByEmail(registrationRequest.email())).willReturn(Optional.of(expecteduserEntity));

        UserAlreadyExistsException actual = assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(registrationRequest));
        //then
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void login_validUser_returnsUserDto() {
        //given
        UserEntity expecteduserEntity = generateUserEntityEnabled();
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();
        UserDto expectedUserDto = userMapper.toUserDto(expecteduserEntity);
        String password = passwordEncoder.encode("password");
        expecteduserEntity.setPassword(password);

        //when
        given(userEntityRepository.findByEmail(credentialsDtoRequest.getEmail())).willReturn(Optional.of(expecteduserEntity));

        UserDto actualResult = userService.login(credentialsDtoRequest);
        //then
        assertEquals(expectedUserDto, actualResult);
    }

    @Test
    void login_userNotVerified_throwsUserNotVerifiedException() {
        //given
        UserEntity expecteduserEntity = generateUserEntity();
        UserNotVerifiedException expected = new UserNotVerifiedException("User not verified", HttpStatus.BAD_REQUEST);
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();

        //when
        given(userEntityRepository.findByEmail(credentialsDtoRequest.getEmail())).willReturn(Optional.of(expecteduserEntity));

        UserNotVerifiedException actualResult = assertThrows(UserNotVerifiedException.class, () -> userService.login(credentialsDtoRequest));
        //then
        assertEquals(expected.getMessage(), actualResult.getMessage());
        assertEquals(expected.getStatus(), actualResult.getStatus());
    }

    @Test
    void login_passwordsDontMatch_throwsInvalidPasswordException() {
        //given
        UserEntity expecteduserEntity = generateUserEntityEnabled();
        InvalidPasswordException expected = new InvalidPasswordException("Invalid password", HttpStatus.BAD_REQUEST);
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();
        String password = passwordEncoder.encode("password1");
        expecteduserEntity.setPassword(password);

        //when
        given(userEntityRepository.findByEmail(credentialsDtoRequest.getEmail())).willReturn(Optional.of(expecteduserEntity));

        InvalidPasswordException actualResult = assertThrows(InvalidPasswordException.class, () -> userService.login(credentialsDtoRequest));
        //then
        assertEquals(expected.getMessage(), actualResult.getMessage());
        assertEquals(expected.getStatus(), actualResult.getStatus());
    }

    @Test
    void login_userNotFound_throwsUserNotFoundException() {
        //given
        UserEntity expecteduserEntity = generateUserEntityEnabled();
        UserNotFoundException expected = new UserNotFoundException("Unknown user", HttpStatus.NOT_FOUND);
        CredentialsDto credentialsDtoRequest = generateCredentialsDto();
        String password = passwordEncoder.encode("password1");
        expecteduserEntity.setPassword(password);

        //when
        given(userEntityRepository.findByEmail(credentialsDtoRequest.getEmail())).willReturn(Optional.empty());

        UserNotFoundException actualResult = assertThrows(UserNotFoundException.class, () -> userService.login(credentialsDtoRequest));
        //then
        assertEquals(expected.getMessage(), actualResult.getMessage());
        assertEquals(expected.getStatus(), actualResult.getStatus());
    }

    @Test
    void findUserByEmailDto_userFound_returnsUserDto() {
        //given
        UserEntity expecteduserEntity = generateUserEntityEnabled();
        UserDto expectedUserDto = userMapper.toUserDto(expecteduserEntity);

        //when
        given(userEntityRepository.findByEmail(expecteduserEntity.getEmail())).willReturn(Optional.of(expecteduserEntity));

        UserDto actualResult = userService.findUserByEmailDto(expecteduserEntity.getEmail());
        //then
        assertEquals(expectedUserDto, actualResult);
    }

    @Test
    void findUserByEmailDto_userNotFound_throwsUserNotFoundException() {
        //given
        UserEntity expecteduserEntity = generateUserEntityEnabled();
        UserNotFoundException expected = new UserNotFoundException("User not found");

        //when
        given(userEntityRepository.findByEmail(expecteduserEntity.getEmail())).willReturn(Optional.empty());

        UserNotFoundException actualResult = assertThrows(UserNotFoundException.class, () -> userService.findUserByEmailDto(expecteduserEntity.getEmail()));
        //then
        assertEquals(expected.getMessage(), actualResult.getMessage());
    }

    @Test
    void validateToken_verifiesToken_returnsOkWithValid() {
        //given
        String expectedResult = "valid";
        String token = "token";
        VerificationToken verificationToken = generateDisabledEntityToken();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 10);
        Date date = new Date(calendar.getTime().getTime());
        verificationToken.setExpiryTime(date);

        //when
        given(verificationTokenRepository.findByToken(token)).willReturn(verificationToken);
        given(userEntityRepository.save(any(UserEntity.class))).willReturn(verificationToken.getUserEntity());
        String result = userService.validateToken("token");

        //then
        assertEquals(expectedResult, result);
        assertTrue(verificationToken.getUserEntity().isEnabled());
    }

    @Test
    void validateToken_nullToken_returnsInvalidMessage() {
        //given
        String expectedResult = "Invalid verification token";

        //when
        String result = userService.validateToken(null);

        //then
        assertEquals(expectedResult, result);
    }

    @Test
    void validateToken_expiredToken_returnsInvalidMessage() {
        //given
        String expectedResult = "Verification token has expired";
        String token = "token";
        VerificationToken verificationToken = generateDisabledEntityToken();

        //when
        given(verificationTokenRepository.findByToken(token)).willReturn(verificationToken);
        String result = userService.validateToken("token");

        //then
        assertEquals(expectedResult, result);
        assertFalse(verificationToken.getUserEntity().isEnabled());
        verifyNoInteractions(userEntityRepository);
    }

    @Test
    void generateNewVerificationToken_generatesNewToken() {
        //given
        String token = "token";
        VerificationToken verificationToken = generateDisabledEntityToken();
        verificationToken.setToken(null);
        Calendar calendar = Calendar.getInstance();

        //when
        given(verificationTokenRepository.findByToken(token)).willReturn(verificationToken);
        given(verificationTokenRepository.save(any(VerificationToken.class))).willReturn(verificationToken);
        VerificationToken result = userService.generateNewVerificationToken(token);

        //then
        assertTrue(verificationToken.getExpiryTime().getTime() - calendar.getTime().getTime() >= 0);
        assertNotNull(result.getToken());

    }

    @Test
    void createPasswordResetToken_createsPasswordResetToken() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String passwordToken = "passwordToken";
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail(userEntity.getEmail());
        passwordResetRequest.setNewPassword("password");
        passwordResetRequest.setConfirmPassword("password");

        //when
        userService.createPasswordResetToken(userEntity, passwordToken, passwordResetRequest);

        //then
        verify(passwordResetTokenService).createPasswordResetTokenForUser(userEntity, passwordToken, passwordResetRequest);
    }

    @Test
    void validatePasswordResetToken_validatesPasswordResetToken() {
        //given
        String passwordToken = "passwordToken";
        String expectedResult = "valid";

        //when
        given(passwordResetTokenService.validatePasswordResetToken(passwordToken)).willReturn(expectedResult);
        String actualResult = userService.validatePasswordResetToken(passwordToken);

        //then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findUserByPasswordToken_findUserByPasswordToken_returnsUserEntity() {
        //given
        String passwordToken = "passwordToken";
        UserEntity expectedUserEntity = generateUserEntityEnabled();

        //when
        given(passwordResetTokenService.findUserByPasswordToken(passwordToken)).willReturn(Optional.of(expectedUserEntity));
        UserEntity actualUserEntity = userService.findUserByPasswordToken(passwordToken);

        //then
        assertEquals(expectedUserEntity, actualUserEntity);
    }

    @Test
    void findUserByPasswordToken_userIsNotFound_throwsUserNotFoundException() {
        //given
        String passwordToken = "passwordToken";
        UserNotFoundException expected = new UserNotFoundException("User not found");

        //when
        given(passwordResetTokenService.findUserByPasswordToken(passwordToken)).willReturn(Optional.empty());
        UserNotFoundException actual = assertThrows(UserNotFoundException.class, () -> userService.findUserByPasswordToken(passwordToken));

        //then
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void resetUserPassword_resetsUserPassword() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String newPassword = "newPassword";

        //when
        userService.resetUserPassword(userEntity, newPassword);

        //then
        assertTrue(passwordEncoder.matches(newPassword, userEntity.getPassword()));
        verify(userEntityRepository).save(userEntity);
    }


}
