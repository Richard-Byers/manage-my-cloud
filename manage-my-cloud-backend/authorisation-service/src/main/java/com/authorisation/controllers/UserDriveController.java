package com.authorisation.controllers;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.mmc.drive.DriveInformationService;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.authorisation.Constants.GOOGLEDRIVE;
import static com.authorisation.Constants.ONEDRIVE;
import static com.authorisation.util.EncryptionUtil.decrypt;

@RestController
@RequiredArgsConstructor
public class UserDriveController {

    private final DriveInformationService driveInformationService;
    private final UserService userService;
    private final CloudPlatformService cloudPlatformService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/drive-information")
    public ResponseEntity<DriveInformationReponse> getUserDriveInformation(@RequestParam("email") String email,
                                                                           @RequestParam("provider") String connectionProvider,
                                                                           @RequestParam("driveEmail") String driveEmail) {

        UserEntity userEntity = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        CloudPlatform cloudPlatform = cloudPlatformService.getUserCloudPlatform(userEntity.getEmail(), connectionProvider, driveEmail);

        if (cloudPlatform == null) {
            throw new RuntimeException(String.format("Cloud platform not found %s", connectionProvider));
        }

        if (connectionProvider.equals(ONEDRIVE)) {
            String accessToken = decrypt(cloudPlatform.getAccessToken());
            Date accessTokenExpiryDate = cloudPlatform.getAccessTokenExpiryDate();
            try {
                DriveInformationReponse drive = driveInformationService.getOneDriveInformation(accessToken, accessTokenExpiryDate);
                return ResponseEntity.ok(drive);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (connectionProvider.equals(GOOGLEDRIVE)) {
            String decryptedRefreshToken = decrypt(cloudPlatform.getRefreshToken());
            String decryptedAccessToken = decrypt(cloudPlatform.getAccessToken());
            try {
                DriveInformationReponse drive = driveInformationService.getGoogleDriveInformation(email, decryptedRefreshToken, decryptedAccessToken);
                return ResponseEntity.ok(drive);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/drive-items")
    public ResponseEntity<JsonNode> getUserDriveFiles(@RequestParam("email") String email,
                                                      @RequestParam("provider") String connectionProvider,
                                                      @RequestParam("driveEmail") String driveEmail) {

        UserEntity userEntity = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        CloudPlatform cloudPlatform = cloudPlatformService.getUserCloudPlatform(userEntity.getEmail(), connectionProvider, driveEmail);

        if (cloudPlatform == null) {
            throw new RuntimeException(String.format("Cloud platform not found %s", connectionProvider));
        }

        if (connectionProvider.equals(ONEDRIVE)) {
            String accessToken = decrypt(cloudPlatform.getAccessToken());
            Date accessTokenExpiryDate = cloudPlatform.getAccessTokenExpiryDate();
            try {
                JsonNode folders = driveInformationService.listAllItemsInOneDrive(accessToken, accessTokenExpiryDate, simpMessagingTemplate, userEntity.getEmail());
                return ResponseEntity.ok(folders);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (connectionProvider.equals(GOOGLEDRIVE)) {
            String decryptedRefreshToken = decrypt(cloudPlatform.getRefreshToken());
            String decryptedAccessToken = decrypt(cloudPlatform.getAccessToken());
            boolean gaveGmailPermissions = cloudPlatform.isGaveGmailPermissions();
            try {
                JsonNode jsonNode = driveInformationService.fetchAllGoogleDriveFiles(decryptedRefreshToken, decryptedAccessToken, simpMessagingTemplate, userEntity.getEmail(), gaveGmailPermissions);
                return ResponseEntity.ok(jsonNode);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/recommend-deletions")
    public ResponseEntity<JsonNode> getRecommendedDeletions(@RequestParam("email") String email,
                                                            @RequestBody JsonNode filesInDrive) {

        UserEntity userEntity = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        UserPreferences userPreferences = userService.getUserRecommendationSettings(userEntity.getEmail());

        try {
            JsonNode recommendedFiles = driveInformationService.returnItemsToDelete(filesInDrive, userPreferences, simpMessagingTemplate, userEntity.getEmail());
            return ResponseEntity.ok(recommendedFiles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/delete-recommended")
    public ResponseEntity<FilesDeletedResponse> deleteRecommendedFiles(@RequestParam("email") String email,
                                                                       @RequestParam("provider") String connectionProvider,
                                                                       @RequestParam("driveEmail") String driveEmail,
                                                                       @RequestBody JsonNode filesToDelete) {

        UserEntity userEntity = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        CloudPlatform cloudPlatform = cloudPlatformService.getUserCloudPlatform(userEntity.getEmail(), connectionProvider, driveEmail);

        if (cloudPlatform == null) {
            throw new RuntimeException(String.format("Cloud platform not found %s", connectionProvider));
        }

        if (connectionProvider.equals(ONEDRIVE)) {
            String accessToken = decrypt(cloudPlatform.getAccessToken());
            Date accessTokenExpiryDate = cloudPlatform.getAccessTokenExpiryDate();
            try {
                FilesDeletedResponse filesDeleted = driveInformationService.deleteRecommendedOneDriveFiles(filesToDelete,
                        accessToken,
                        accessTokenExpiryDate,
                        simpMessagingTemplate,
                        userEntity.getEmail());
                return ResponseEntity.ok().body(filesDeleted);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (connectionProvider.equals(GOOGLEDRIVE)) {
            String decryptedRefreshToken = decrypt(cloudPlatform.getRefreshToken());
            String decryptedAccessToken = decrypt(cloudPlatform.getAccessToken());
            try {
                FilesDeletedResponse filesDeleted = driveInformationService.deleteRecommendedGoogleDriveFiles(filesToDelete,
                        decryptedRefreshToken,
                        decryptedAccessToken,
                        simpMessagingTemplate,
                        userEntity.getEmail());
                return ResponseEntity.ok().body(filesDeleted);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/get-duplicates")
    public ResponseEntity<JsonNode> getAIDuplicatesResponse(@RequestParam("email") String email,
                                                            @RequestParam("provider") String connectionProvider,
                                                            @RequestParam("driveEmail") String driveEmail,
                                                            @RequestBody JsonNode files) {

        UserEntity userEntity = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        CloudPlatform cloudPlatform = cloudPlatformService.getUserCloudPlatform(userEntity.getEmail(), connectionProvider, driveEmail);

        if (cloudPlatform == null) {
            throw new RuntimeException(String.format("Cloud platform not found %s", connectionProvider));
        }

        if (connectionProvider.equals(ONEDRIVE)) {
            try {
                JsonNode jsonNode = driveInformationService.getDuplicatesFoundByAI( ONEDRIVE, files);
                return ResponseEntity.ok(jsonNode);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (connectionProvider.equals(GOOGLEDRIVE)) {
            try {
                JsonNode jsonNode = driveInformationService.getDuplicatesFoundByAI( GOOGLEDRIVE, files);
                return ResponseEntity.ok(jsonNode);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
