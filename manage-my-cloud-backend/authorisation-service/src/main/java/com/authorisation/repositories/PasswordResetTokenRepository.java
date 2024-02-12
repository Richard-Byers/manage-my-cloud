package com.authorisation.repositories;


import com.authorisation.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String verificationToken);

    Optional<PasswordResetToken> findByUserEntityId(Long id);
}
