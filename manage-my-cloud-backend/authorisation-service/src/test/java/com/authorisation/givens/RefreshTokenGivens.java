package com.authorisation.givens;

import com.authorisation.entities.RefreshToken;
import com.authorisation.entities.UserEntity;

import java.time.Instant;

public class RefreshTokenGivens {

    public static RefreshToken generateRefreshToken(UserEntity userEntity) {
        return RefreshToken.builder()
                .id(1L)
                .token("token")
                .expiryDate(Instant.now().plusMillis(3600000))
                .userEntity(userEntity)
                .build();
    }
}
