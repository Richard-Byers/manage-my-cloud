package com.authorisation.mappers;

import com.authorisation.entities.RecommendationSettings;
import org.mapstruct.Mapper;
import org.mmc.pojo.UserPreferences;

@Mapper(componentModel = "spring")
public interface UserPreferencesMapper {

    UserPreferences toUserPreferences(RecommendationSettings recommendationSettings);
}
