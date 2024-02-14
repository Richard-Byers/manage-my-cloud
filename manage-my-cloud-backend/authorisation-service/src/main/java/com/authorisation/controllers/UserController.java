package com.authorisation.controllers;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/update-profile-Img")
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

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestBody CredentialsDto credentialsDto) {
        try {
            userService.deleteUser(credentialsDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/data-request")
    public ResponseEntity<Resource> getUserData(@RequestParam String email) {
        Optional<UserEntity> userOptional = userService.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserEntity user = userOptional.get();
        String data = userService.getUserData(user);
        if (data == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Path path = Paths.get("user-data.txt");
            Files.writeString(path, data);

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Error writing user data to file", e);
        }
    }

    @PostMapping("/update-user-details")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDto userDetailsDto) {
        try {
            UserEntity user = userService.findUserByEmail(userDetailsDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userService.updateDetails(user, userDetailsDto.getFirstName(), userDetailsDto.getLastName());

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}