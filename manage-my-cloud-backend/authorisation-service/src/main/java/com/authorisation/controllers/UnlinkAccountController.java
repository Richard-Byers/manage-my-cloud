package com.authorisation.controllers;

import com.authorisation.services.OneDriveService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnlinkAccountController {

    private final OneDriveService oneDriveService;

    @DeleteMapping("/unlink-drive")
    public void unlinkOneDrive(@RequestParam("email") String email, @RequestParam("provider") String provider) {

        if (provider.equals("OneDrive")) {
            oneDriveService.unlinkOneDrive(email);
        }
    }

}
