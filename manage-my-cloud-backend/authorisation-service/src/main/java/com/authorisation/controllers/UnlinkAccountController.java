package com.authorisation.controllers;

import com.authorisation.services.GoogleAuthService;
import com.authorisation.services.OneDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnlinkAccountController {

    private final OneDriveService oneDriveService;
    private final GoogleAuthService googleAuthService;

    @DeleteMapping("/unlink-drive")
    public void unlinkOneDrive(@RequestParam("email") String email, @RequestParam("provider") String provider, @RequestParam("driveEmail") String driveEmail) {

        if (provider.equals("OneDrive")) {
            oneDriveService.unlinkOneDrive(email, driveEmail);
        } else if (provider.equals("GoogleDrive")) {
            googleAuthService.unlinkGoogleDrive(email, driveEmail);
        }
    }

}
