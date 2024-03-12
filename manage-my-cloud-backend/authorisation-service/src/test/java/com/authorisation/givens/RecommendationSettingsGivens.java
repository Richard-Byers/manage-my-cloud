package com.authorisation.givens;

import com.authorisation.entities.RecommendationSettings;
import com.authorisation.entities.UserEntity;
import org.mmc.pojo.UserPreferences;

public class RecommendationSettingsGivens {

    public static UserPreferences generateUserPreferences() {
        return UserPreferences.builder().deleteDocuments(true)
                .deleteEmails(true)
                .deleteImages(true)
                .deleteVideos(true)
                .deleteItemsCreatedAfterDays(7)
                .deleteItemsNotChangedSinceDays(7).build();
    }

    public static RecommendationSettings generateRecommendationSettings(UserEntity user) {
        return new RecommendationSettings(1L, user, true, true, true, true, 7, 7, 7);
    }
}
