package com.authorisation.controllers;

import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/updateProfileImg")
    public ResponseEntity<?> updateProfileImg(@NotNull @RequestParam("image") MultipartFile image, @RequestParam("email") String email) {
        try {
            UserEntity user = userService.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            byte[] newImage = image.getBytes();
            userService.updateProfileImage(user, newImage);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error updating profile image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add other methods here with their own mappings, like /updatename, etc.
}
