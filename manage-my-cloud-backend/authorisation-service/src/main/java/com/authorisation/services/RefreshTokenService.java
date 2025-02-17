package com.authorisation.services;

import com.authorisation.entities.RefreshToken;
import com.authorisation.repositories.RefreshTokenRepository;
import com.authorisation.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserEntityRepository userEntityRepository;

    public RefreshToken createRefreshtoken(String email) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserEntityEmail(email);

        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            // If a token already exists, update it
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(3600000)); // 1 hour
        } else {
            // If no token exists, create a new one
            refreshToken = RefreshToken.builder()
                    .userEntity(userEntityRepository.findByEmail(email).get())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(3600000)) // 1 hour
                    .build();
        }

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken findByUserEntityEmail(String email) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserEntityEmail(email);
        if (refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token not found for user with email: " + email);
        }
        return refreshToken.get();
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token has expired, please make a new sign in request.");

        }
        return token;
    }
}
