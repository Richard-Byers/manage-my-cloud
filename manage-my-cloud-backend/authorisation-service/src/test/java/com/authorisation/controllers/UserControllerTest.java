package com.authorisation.controllers;

import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private UserEntity testUser;
    private MockMultipartFile testImage;
    private static final String UPDATE_PROFILE_IMG_URL = "/update-profile-Img";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        this.testUser = new UserEntity();
        this.testUser.setEmail("test@example.com");
        this.testImage = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test image content".getBytes());

        given(userService.findUserByEmail(testUser.getEmail())).willReturn(Optional.of(testUser));
    }

    @Test
    void updateProfileImg_ValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(multipart(UPDATE_PROFILE_IMG_URL)
                        .file(testImage)
                        .param("email", testUser.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    void updateProfileImg_UserNotFound_ReturnsNotFound() throws Exception {
        given(userService.findUserByEmail(testUser.getEmail())).willThrow(new RuntimeException("User not found"));

        mockMvc.perform(multipart(UPDATE_PROFILE_IMG_URL)
                        .file(testImage)
                        .param("email", testUser.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfileImg_UpdateProfileImageThrowsIOException_ReturnsInternalServerError() throws Exception {
        given(userService.findUserByEmail(testUser.getEmail())).willReturn(Optional.of(testUser));
        willThrow(new RuntimeException("Error updating profile image")).given(userService).updateProfileImage(testUser, testImage.getBytes());

        mockMvc.perform(multipart(UPDATE_PROFILE_IMG_URL)
                        .file(testImage)
                        .param("email", testUser.getEmail()))
                .andExpect(status().isNotFound());
    }
}
