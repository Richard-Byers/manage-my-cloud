package com.authorisation.controllers;
import com.authorisation.entities.RecommendationSettings;
import com.authorisation.entities.UserEntity;
import com.authorisation.repositories.RecommendationSettingsRepository;
import com.authorisation.services.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RecommendationSettingsController {

    @NonNull
    private RecommendationSettingsRepository recommendationSettingsRepository;
    private static final Logger logger = LoggerFactory.getLogger(RecommendationSettingsController.class);

    private final UserService userService;
    @GetMapping("get-preferences/{email}")
    public RecommendationSettings getRecommendationSettings(@PathVariable String email) {
        UserEntity user = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        RecommendationSettings existingSettings = recommendationSettingsRepository.findByUserEntityEmail(email);
        if (existingSettings == null) {
            throw new RuntimeException("RecommendationSettings not found for email: " + email);
        }
        return existingSettings;
    }

    @PostMapping("/preference-update")
    public RecommendationSettings updateRecommendationSettings(@RequestBody RecommendationSettings newSettings, @RequestParam String email) {
        UserEntity user = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        newSettings.setUserEntity(user);

        RecommendationSettings existingSettings = recommendationSettingsRepository.findByUserEntityEmail(email);

        if (existingSettings != null) {
            existingSettings.setDeleteVideos(newSettings.isDeleteVideos());
            existingSettings.setDeleteImages(newSettings.isDeleteImages());
            existingSettings.setDeleteDocuments(newSettings.isDeleteDocuments());
            existingSettings.setDeleteEmails(newSettings.isDeleteEmails());
            existingSettings.setDeleteEmailsAfterDays(newSettings.getDeleteEmailsAfterDays());
            existingSettings.setDeleteItemsCreatedAfterDays(newSettings.getDeleteItemsCreatedAfterDays());
            existingSettings.setDeleteItemsNotChangedSinceDays(newSettings.getDeleteItemsNotChangedSinceDays());

            return recommendationSettingsRepository.save(existingSettings);
        } else {
            throw new RuntimeException("RecommendationSettings not found for email: " + email);
        }
    }
}