package com.authorisation.repositories;

import com.authorisation.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>{
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserEntityEmail(String email);

    void deleteByUserEntityId(long id);
}
