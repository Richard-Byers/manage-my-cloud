package com.authorisation.controllers;

import com.authorisation.controllers.UserDriveController;
import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mmc.drive.DriveInformationService;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.authorisation.givens.CloudPlatformGivens.generateCloudPlatformEncryptedTokens;
import static com.authorisation.givens.DriveInformationResponseGivens.generateDriveInformationResponse;
import static com.authorisation.givens.FilesDeleteGivens.generateFilesDeletedResponse;
import static com.authorisation.givens.JsonNodeGivens.generateJsonNode;
import static com.authorisation.givens.RecommendationSettingsGivens.generateUserPreferences;
import static com.authorisation.givens.UserEntityGivens.generateUserEntityEnabled;
import static com.authorisation.util.EncryptionUtil.decrypt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserDriveController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserDriveControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private UserService userService;
    @MockBean
    private CloudPlatformService cloudPlatformService;
    @MockBean
    private DriveInformationService driveInformationService;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_ValidRequest_ReturnsDriveInformation() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        DriveInformationReponse expectedDriveInformationReponse = generateDriveInformationResponse();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/drive-information")
                        .param("email", email)
                        .param("provider", "OneDrive").param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        DriveInformationReponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DriveInformationReponse.class);
        assertDriveInformationResponse(expectedDriveInformationReponse, response);
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_InvalidUser_ThrowsException() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get("/drive-information")
                .param("email", email)
                .param("provider", "OneDrive").param("driveEmail", driveEmail).with(csrf())).andReturn());
        //then
        assertEquals("Cloud platform not found OneDrive", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_CloudPlatformNull_ThrowsException() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();

        when(userService.findUserByEmail(email)).thenThrow(new RuntimeException("User not found"));

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get("/drive-information")
                .param("email", email)
                .param("provider", "OneDrive").param("driveEmail", email).with(csrf())).andReturn());

        assertEquals("User not found", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_GetOneDriveInformation_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(any(), any())).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get("/drive-information")
                        .param("email", email)
                        .param("provider", "OneDrive").param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_IncorrectProvider_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        DriveInformationReponse expectedDriveInformationReponse = generateDriveInformationResponse();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "random", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        mockMvc.perform(get("/drive-information")
                        .param("email", email)
                        .param("provider", "random").param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_ValidRequest_ReturnsDriveInformation() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        JsonNode expectedJsonNode = generateJsonNode();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.listAllItemsInOneDrive(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/drive-items")
                        .param("email", email)
                        .param("provider", "OneDrive").param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        JsonNode response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode.class);
        assertJsonNodeResponse(expectedJsonNode, response);
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_InvalidUser_ThrowsException() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get("/drive-items")
                .param("email", email)
                .param("provider", "OneDrive").param("driveEmail", driveEmail).with(csrf())).andReturn());
        //then
        assertEquals("Cloud platform not found OneDrive", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_CloudPlatformNull_ThrowsException() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();

        when(userService.findUserByEmail(email)).thenThrow(new RuntimeException("User not found"));

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get("/drive-items")
                .param("email", email)
                .param("provider", "OneDrive").param("driveEmail", email).with(csrf())).andReturn());

        assertEquals("User not found", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_GetOneDriveInformation_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.listAllItemsInOneDrive(any(), any())).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get("/drive-items")
                        .param("email", email)
                        .param("provider", "OneDrive").param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_IncorrectProvider_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        DriveInformationReponse expectedDriveInformationReponse = generateDriveInformationResponse();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "random", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        mockMvc.perform(get("/drive-items")
                        .param("email", email)
                        .param("provider", "random").param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void recommendDeletions_validRequest_ReturnsRecommendations() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        UserPreferences preferences = generateUserPreferences();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userService.getUserRecommendationSettings(email)).thenReturn(preferences);
        when(driveInformationService.returnItemsToDelete(expectedJsonNode, preferences)).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/recommend-deletions")
                        .param("email", email).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        JsonNode response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode.class);
        assertJsonNodeResponse(expectedJsonNode, response);
    }

    @Test
    @WithMockUser
    void recommendDeletions_returnItemsToDelete_throwsException() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        UserPreferences preferences = generateUserPreferences();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userService.getUserRecommendationSettings(email)).thenReturn(preferences);
        when(driveInformationService.returnItemsToDelete(expectedJsonNode, preferences)).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post("/recommend-deletions")
                        .param("email", email).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteRecommendedFiles_validRequest_ReturnsRecommendations() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        UserPreferences preferences = generateUserPreferences();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        String accessToken = decrypt(cloudPlatform.getAccessToken());
        FilesDeletedResponse expectedFilesDeletedResponse = generateFilesDeletedResponse();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userService.getUserRecommendationSettings(email)).thenReturn(preferences);
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.deleteRecommendedOneDriveFiles(expectedJsonNode, accessToken, cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedFilesDeletedResponse);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/delete-recommended")
                        .param("email", email).param("provider", "OneDrive").param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        FilesDeletedResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FilesDeletedResponse.class);
        assertEquals(expectedFilesDeletedResponse.getFilesDeleted(), response.getFilesDeleted());
    }

    @Test
    @WithMockUser
    void deleteRecommendedFiles_userNotFound_throwsException() {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();

        when(userService.findUserByEmail(email)).thenThrow(new RuntimeException("User not found"));

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(post("/delete-recommended")
                        .param("email", email).param("provider", "OneDrive").param("driveEmail", email).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()));

        assertEquals("User not found", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void deleteRecommendedFiles_cloudPlatformNotFound_throwsException() {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenThrow(new RuntimeException("Cloud platform not found"));

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(post("/delete-recommended")
                        .param("email", email).param("provider", "OneDrive").param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()));

        assertEquals("Cloud platform not found", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void deleteRecommendedFiles_driveInformationService_throwsException() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        UserPreferences preferences = generateUserPreferences();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        String accessToken = decrypt(cloudPlatform.getAccessToken());
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userService.getUserRecommendationSettings(email)).thenReturn(preferences);
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive", driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.deleteRecommendedOneDriveFiles(expectedJsonNode, accessToken, cloudPlatform.getAccessTokenExpiryDate())).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post("/delete-recommended")
                        .param("email", email).param("provider", "OneDrive").param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteRecommendedFiles_unsupportedProvider_returnsBadRequest() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        UserPreferences preferences = generateUserPreferences();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userService.getUserRecommendationSettings(email)).thenReturn(preferences);
        when(cloudPlatformService.getUserCloudPlatform(email, "random", driveEmail)).thenReturn(cloudPlatform);

        //when
        mockMvc.perform(post("/delete-recommended")
                        .param("email", email).param("provider", "random").param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    //Helper
    private static void assertDriveInformationResponse(DriveInformationReponse expectedDriveInformationReponse, DriveInformationReponse response) {
        assertEquals(expectedDriveInformationReponse.getDisplayName(), response.getDisplayName());
        assertEquals(expectedDriveInformationReponse.getTotal(), response.getTotal());
        assertEquals(expectedDriveInformationReponse.getUsed(), response.getUsed());
    }

    private static void assertJsonNodeResponse(JsonNode expectedJsonNode, JsonNode response) {
        assertEquals(expectedJsonNode.get("test"), response.get("test"));
    }


}
