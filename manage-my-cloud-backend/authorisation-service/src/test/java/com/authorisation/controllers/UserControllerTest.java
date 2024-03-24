package com.authorisation.controllers;

import com.authorisation.dto.CredentialsDto;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.authorisation.TestConstants.TESTER_EMAIL;
import static com.authorisation.givens.UserEntityGivens.generateUserData;
import static com.authorisation.givens.UserEntityGivens.generateUserEntityTesterEmail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String UPDATE_PROFILE_IMG_URL = "/update-profile-Img";
    private static final String DELETE_USER_URL = "/delete-user";
    private static final String GET_USER_DATA_URL = "/data-request";
    private static final String UPDATE_USER_DETAILS_URL = "/update-user-details";
    private UserEntity testUser;
    private MockMultipartFile testImage;
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

    @AfterAll
    public static void tearDown() throws IOException {
        Files.delete(Paths.get("user-data.txt"));
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
    void updateProfileImg_UpdateProfileImageThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
        given(userService.findUserByEmail(testUser.getEmail())).willReturn(Optional.of(testUser));
        willThrow(new RuntimeException("Error updating profile image")).given(userService).updateProfileImage(testUser, testImage.getBytes());

        mockMvc.perform(multipart(UPDATE_PROFILE_IMG_URL)
                        .file(testImage)
                        .param("email", testUser.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ValidRequest_ReturnsOk() throws Exception {
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.setEmail(testUser.getEmail());
        credentialsDto.setPassword("password");

        mockMvc.perform(delete(DELETE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + credentialsDto.getEmail() + "\",\"password\":\"" + credentialsDto.getPassword() + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_DeleteUserError_ThrowsRuntimeException() throws Exception {
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.setEmail(testUser.getEmail());
        credentialsDto.setPassword("password");
        willThrow(new RuntimeException("User not found")).given(userService).deleteUser(credentialsDto);

        mockMvc.perform(delete(DELETE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + credentialsDto.getEmail() + "\",\"password\":\"" + credentialsDto.getPassword() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserData_ValidRequest_ReturnsUserData() throws Exception {
        String email = TESTER_EMAIL;
        String userData = generateUserData();
        UserEntity user = generateUserEntityTesterEmail();
        given(userService.findUserByEmail(email)).willReturn(Optional.of(user));
        given(userService.getUserData(user)).willReturn(userData);

        MvcResult result = mockMvc.perform(post(GET_USER_DATA_URL)
                        .param("email", email))
                .andExpect(status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(userData, content);
    }

    @Test
    void getUserData_NullUserData_ReturnNotFound() throws Exception {
        String email = TESTER_EMAIL;
        UserEntity user = generateUserEntityTesterEmail();
        given(userService.findUserByEmail(email)).willReturn(Optional.of(user));
        given(userService.getUserData(user)).willReturn(null);

        mockMvc.perform(post(GET_USER_DATA_URL)
                        .param("email", email))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserData_UserNotFound_ReturnsNotFound() throws Exception {
        String nonExistentEmail = "unknown@example.com";
        given(userService.findUserByEmail(nonExistentEmail)).willReturn(Optional.empty());

        mockMvc.perform(post(GET_USER_DATA_URL)
                        .param("email", nonExistentEmail))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserDetails_ValidRequest_ReturnsOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail(testUser.getEmail());
        userDto.setFirstName("NewFirstName");
        userDto.setLastName("NewLastName");

        mockMvc.perform(post(UPDATE_USER_DETAILS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + userDto.getEmail() + "\",\"firstName\":\"" + userDto.getFirstName() + "\",\"lastName\":\"" + userDto.getLastName() + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserDetails_UserNotFound_ReturnsNotFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("unknown@example.com");
        userDto.setFirstName("NewFirstName");
        userDto.setLastName("NewLastName");

        mockMvc.perform(post(UPDATE_USER_DETAILS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + userDto.getEmail() + "\",\"firstName\":\"" + userDto.getFirstName() + "\",\"lastName\":\"" + userDto.getLastName() + "\"}"))
                .andExpect(status().isNotFound());
    }
}

