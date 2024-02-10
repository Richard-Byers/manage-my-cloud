package com.authorisation.services;

import com.authorisation.entities.RefreshToken;
import com.authorisation.entities.UserEntity;
import com.authorisation.repositories.RefreshTokenRepository;
import com.authorisation.repositories.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void createRefreshtoken_existingToken_updatesToken() {
        // Arrange
        String email = "test@example.com";
        RefreshToken existingToken = new RefreshToken();
        existingToken.setToken(UUID.randomUUID().toString());
        existingToken.setExpiryDate(Instant.now().plusMillis(3600000)); // 1 hour
        when(refreshTokenRepository.findByUserEntityEmail(email)).thenReturn(Optional.of(existingToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(existingToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshtoken(email);

        // Assert
        assertNotNull(result.getToken());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshtoken_noExistingToken_createsNewToken() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        when(refreshTokenRepository.findByUserEntityEmail(email)).thenReturn(Optional.empty());
        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        RefreshToken expectedRefreshToken = new RefreshToken();
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expectedRefreshToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshtoken(email);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRefreshToken, result);
    }

    @Test
    void findByToken_existingToken_returnsToken() {
        // Arrange
        String token = UUID.randomUUID().toString();
        RefreshToken existingToken = new RefreshToken();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(existingToken));

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(existingToken, result.get());
    }

    @Test
    void verifyExpiration_tokenNotExpired_returnsToken() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(3600000)); // 1 hour in the future

        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // Assert
        assertEquals(token, result);
    }

    @Test
    void verifyExpiration_tokenExpired_throwsException() {
        // Arrange
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusMillis(3600000)); // 1 hour in the past

        // Act and Assert
        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));
    }
}
