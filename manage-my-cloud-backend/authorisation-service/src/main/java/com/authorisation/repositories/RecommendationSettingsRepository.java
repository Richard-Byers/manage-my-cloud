package com.authorisation.repositories;

import com.authorisation.entities.RecommendationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationSettingsRepository extends JpaRepository<RecommendationSettings, Long> {

    RecommendationSettings findByUserEntityEmail(String email);
}
