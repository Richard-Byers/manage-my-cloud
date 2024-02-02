package com.authorisation.controllers;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.mmc.drive.DriveInformationService;
import org.mmc.response.DriveInformationReponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/drive-information")
    public ResponseEntity<DriveInformationReponse> getUserDriveInformation(@RequestParam("email") String email, @RequestParam("provider") String connectionProvider) {

        UserEntity userEntity = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        CloudPlatform cloudPlatform = cloudPlatformService.getUserCloudPlatform(userEntity.getEmail(), connectionProvider);

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
            DriveInformationReponse drive = new DriveInformationReponse("Google Drive", "placeholder", 0L, 0.0);
            return ResponseEntity.ok(drive);
        }

        return ResponseEntity.badRequest().build();
    }


}
