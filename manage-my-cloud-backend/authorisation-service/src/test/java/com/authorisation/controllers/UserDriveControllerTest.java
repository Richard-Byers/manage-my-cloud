package com.authorisation.controllers;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.OneDriveService;
import com.authorisation.services.UserService;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.authorisation.TestConstants.GOOGLEDRIVE;
import static com.authorisation.TestConstants.ONEDRIVE;
import static com.authorisation.givens.CloudPlatformGivens.generateCloudPlatformEncryptedTokens;
import static com.authorisation.givens.CloudPlatformGivens.generateGoogleCloudPlatformEncryptedTokens;
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
    @MockBean
    private OneDriveService oneDriveService;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    ObjectMapper objectMapper = new ObjectMapper();
    private static final String DRIVE_INFORMATION_URL = "/drive-information";
    private static final String DRIVE_ITEMS_URL = "/drive-items";
    private static final String RECOMMEND_DELETIONS_URL = "/recommend-deletions";
    private static final String RECOMMEND_DUPLICATES_URL = "/get-duplicates";
    private static final String DELETE_RECOMMENDED_URL = "/delete-recommended";

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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(cloudPlatformService.isTokenRefreshNeeded(email, ONEDRIVE, driveEmail)).thenReturn(false);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        MvcResult mvcResult = mockMvc.perform(get(DRIVE_INFORMATION_URL)
                        .param("email", email)
                        .param("provider", ONEDRIVE)
                        .param("driveEmail", driveEmail)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        //then
        DriveInformationReponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DriveInformationReponse.class);
        assertDriveInformationResponse(expectedDriveInformationReponse, response);
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_ValidRequestGoogleDrive_ReturnsDriveInformation() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();
        DriveInformationReponse expectedDriveInformationReponse = generateDriveInformationResponse();
        String refreshToken = decrypt(cloudPlatform.getRefreshToken());
        String accessToken = decrypt(cloudPlatform.getAccessToken());
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, GOOGLEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getGoogleDriveInformation(email, refreshToken, accessToken)).thenReturn(expectedDriveInformationReponse);

        //when
        MvcResult mvcResult = mockMvc.perform(get(DRIVE_INFORMATION_URL)
                        .param("email", email)
                        .param("provider", GOOGLEDRIVE).param("driveEmail", driveEmail).with(csrf()))
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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get(DRIVE_INFORMATION_URL)
                .param("email", email)
                .param("provider", ONEDRIVE)
                .param("driveEmail", driveEmail)
                .with(csrf()))
        );

        //then
        assertEquals("Cloud platform not found OneDrive", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_CloudPlatformNull_ThrowsException() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(null);
        when(cloudPlatformService.isTokenRefreshNeeded(email, ONEDRIVE, driveEmail)).thenReturn(false);

        //when
        ServletException exception = assertThrows(ServletException.class, () ->
                mockMvc.perform(get(DRIVE_INFORMATION_URL)
                        .param("email", email)
                        .param("provider", ONEDRIVE)
                        .param("driveEmail", driveEmail)
                        .with(csrf()))
        );

        //then
        assertEquals("Cloud platform not found OneDrive", exception.getRootCause().getMessage());
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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(any(), any())).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get(DRIVE_INFORMATION_URL)
                        .param("email", email)
                        .param("provider", ONEDRIVE).param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_GetGoogleDriveInformation_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();
        String refreshToken = decrypt(cloudPlatform.getRefreshToken());
        String accessToken = decrypt(cloudPlatform.getAccessToken());
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, GOOGLEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.getGoogleDriveInformation(email, refreshToken, accessToken)).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get(DRIVE_INFORMATION_URL)
                        .param("email", email)
                        .param("provider", GOOGLEDRIVE).param("driveEmail", driveEmail).with(csrf()))
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
        mockMvc.perform(get(DRIVE_INFORMATION_URL)
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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.listAllItemsInOneDrive(decrypt(cloudPlatform.getAccessToken()),
                cloudPlatform.getAccessTokenExpiryDate(),
                simpMessagingTemplate,
                email)).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(get(DRIVE_ITEMS_URL)
                        .param("email", email)
                        .param("provider", ONEDRIVE).param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        JsonNode response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode.class);
        assertJsonNodeResponse(expectedJsonNode, response);
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_ValidRequestGoogleDrive_ReturnsDriveInformation() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();
        JsonNode expectedJsonNode = new ObjectMapper().readTree("{\"key\":\"value\"}");
        String refreshToken = decrypt(cloudPlatform.getRefreshToken());
        String accessToken = decrypt(cloudPlatform.getAccessToken());
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, GOOGLEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.fetchAllGoogleDriveFiles(refreshToken, accessToken, simpMessagingTemplate,
                email, true)).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(get(DRIVE_ITEMS_URL)
                        .param("email", email)
                        .param("provider", GOOGLEDRIVE).param("driveEmail", driveEmail).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        if (!responseBody.isEmpty()) {
            JsonNode response = objectMapper.readValue(responseBody, JsonNode.class);
            assertJsonNodeResponse(expectedJsonNode, response);
        }
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_InvalidUser_ThrowsException() {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get(DRIVE_ITEMS_URL)
                .param("email", email)
                .param("provider", ONEDRIVE).param("driveEmail", driveEmail).with(csrf())).andReturn());
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
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get(DRIVE_ITEMS_URL)
                .param("email", email)
                .param("provider", ONEDRIVE).param("driveEmail", email).with(csrf())).andReturn());

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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.listAllItemsInOneDrive(any(), any(), any(), any())).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get(DRIVE_ITEMS_URL)
                        .param("email", email)
                        .param("provider", ONEDRIVE).param("driveEmail", driveEmail).with(csrf()))
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
        mockMvc.perform(get(DRIVE_ITEMS_URL)
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
        when(driveInformationService.returnItemsToDelete(expectedJsonNode, preferences, simpMessagingTemplate, email)).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(post(RECOMMEND_DELETIONS_URL)
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
        when(driveInformationService.returnItemsToDelete(expectedJsonNode, preferences, simpMessagingTemplate, email)).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post(RECOMMEND_DELETIONS_URL)
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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.deleteRecommendedOneDriveFiles(expectedJsonNode,
                accessToken,
                cloudPlatform.getAccessTokenExpiryDate(),
                simpMessagingTemplate,
                email)).thenReturn(expectedFilesDeletedResponse);

        //when
        MvcResult mvcResult = mockMvc.perform(post(DELETE_RECOMMENDED_URL)
                        .param("email", email).param("provider", ONEDRIVE).param("driveEmail", driveEmail).contentType("application/json")
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
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(post(DELETE_RECOMMENDED_URL)
                        .param("email", email).param("provider", ONEDRIVE).param("driveEmail", email).contentType("application/json")
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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(post(DELETE_RECOMMENDED_URL)
                        .param("email", email).param("provider", ONEDRIVE).param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()));

        assertEquals("Cloud platform not found OneDrive", exception.getRootCause().getMessage());
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
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.deleteRecommendedOneDriveFiles(expectedJsonNode,
                accessToken,
                cloudPlatform.getAccessTokenExpiryDate(),
                simpMessagingTemplate,
                email)).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post(DELETE_RECOMMENDED_URL)
                        .param("email", email).param("provider", ONEDRIVE).param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteRecommendedFiles_driveInformationServiceGoogleDrive_throwsException() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        UserPreferences preferences = generateUserPreferences();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();
        String accessToken = decrypt(cloudPlatform.getAccessToken());
        String refreshToken = decrypt(cloudPlatform.getRefreshToken());
        String driveEmail = "email2@example.com";

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userService.getUserRecommendationSettings(email)).thenReturn(preferences);
        when(cloudPlatformService.getUserCloudPlatform(email, GOOGLEDRIVE, driveEmail)).thenReturn(cloudPlatform);
        when(driveInformationService.deleteRecommendedGoogleDriveFiles(expectedJsonNode, refreshToken, accessToken, simpMessagingTemplate,
                email)).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post(DELETE_RECOMMENDED_URL)
                        .param("email", email).param("provider", GOOGLEDRIVE).param("driveEmail", driveEmail).contentType("application/json")
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
        mockMvc.perform(post(DELETE_RECOMMENDED_URL)
                        .param("email", email).param("provider", "random").param("driveEmail", driveEmail).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAIDuplicatesResponse_validOneDriveRequest_ReturnsDuplicates() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, email)).thenReturn(cloudPlatform);
        when(driveInformationService.getDuplicatesFoundByAI(ONEDRIVE, expectedJsonNode)).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(post(RECOMMEND_DUPLICATES_URL)
                        .param("email", email).contentType("application/json")
                        .param("provider", ONEDRIVE)
                        .param("driveEmail", email)
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        JsonNode response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode.class);
        assertJsonNodeResponse(expectedJsonNode, response);
    }

    @Test
    @WithMockUser
    void getAIDuplicatesResponse_OneDriveThrowsException_ReturnsBadRequest() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, ONEDRIVE, email)).thenReturn(cloudPlatform);
        when(driveInformationService.getDuplicatesFoundByAI(ONEDRIVE, expectedJsonNode)).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post(RECOMMEND_DUPLICATES_URL)
                        .param("email", email).contentType("application/json")
                        .param("provider", ONEDRIVE)
                        .param("driveEmail", email)
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser
    void getAIDuplicatesResponse_validGoogleDriveRequest_ReturnsDuplicates() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, GOOGLEDRIVE, email)).thenReturn(cloudPlatform);
        when(driveInformationService.getDuplicatesFoundByAI(GOOGLEDRIVE, expectedJsonNode)).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(post(RECOMMEND_DUPLICATES_URL)
                        .param("email", email).contentType("application/json")
                        .param("provider", GOOGLEDRIVE)
                        .param("driveEmail", email)
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()).andReturn();

        JsonNode response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JsonNode.class);
        assertJsonNodeResponse(expectedJsonNode, response);
    }

    @Test
    @WithMockUser
    void getAIDuplicatesResponse_GoogleDriveThrowsException_ReturnsBadRequest() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, GOOGLEDRIVE, email)).thenReturn(cloudPlatform);
        when(driveInformationService.getDuplicatesFoundByAI(GOOGLEDRIVE, expectedJsonNode)).thenThrow(new RuntimeException("Error"));

        //when
        mockMvc.perform(post(RECOMMEND_DUPLICATES_URL)
                        .param("email", email).contentType("application/json")
                        .param("provider", GOOGLEDRIVE)
                        .param("driveEmail", email)
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser
    void getAIDuplicatesResponse_invalidProvider_ReturnsBadRequest() throws Exception {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "random", email)).thenReturn(cloudPlatform);

        //when
        mockMvc.perform(post(RECOMMEND_DUPLICATES_URL)
                        .param("email", email).contentType("application/json")
                        .param("provider", "random")
                        .param("driveEmail", email)
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser
    void getAIDuplicatesResponse_cloudPlatformNotFound_ReturnsBadRequest() {
        //given
        JsonNode expectedJsonNode = generateJsonNode();
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateGoogleCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "random", email)).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(post(RECOMMEND_DUPLICATES_URL)
                        .param("email", email).param("provider", ONEDRIVE).param("driveEmail", email).contentType("application/json")
                        .content(objectMapper.writeValueAsString(expectedJsonNode)).with(csrf()))
                //then
                .andExpect(status().isOk()));

        assertEquals("Cloud platform not found OneDrive", exception.getRootCause().getMessage());
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
