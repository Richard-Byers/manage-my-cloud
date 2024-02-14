package com.authorisation.controllers;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mmc.drive.DriveInformationService;
import org.mmc.response.DriveInformationReponse;
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
import static com.authorisation.givens.JsonNodeGivens.generateJsonNode;
import static com.authorisation.givens.UserEntityGivens.generateUserEntityEnabled;
import static com.authorisation.util.EncryptionUtil.decrypt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserDriveController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserDriveControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();
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

    //Helper
    private static void assertDriveInformationResponse(DriveInformationReponse expectedDriveInformationReponse, DriveInformationReponse response) {
        assertEquals(expectedDriveInformationReponse.getDisplayName(), response.getDisplayName());
        assertEquals(expectedDriveInformationReponse.getDriveType(), response.getDriveType());
        assertEquals(expectedDriveInformationReponse.getTotal(), response.getTotal());
        assertEquals(expectedDriveInformationReponse.getUsed(), response.getUsed());
    }

    private static void assertJsonNodeResponse(JsonNode expectedJsonNode, JsonNode response) {
        assertEquals(expectedJsonNode.get("test"), response.get("test"));
    }

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

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive")).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/drive-information")
                        .param("email", email)
                        .param("provider", "OneDrive").with(csrf()))
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

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive")).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get("/drive-information")
                .param("email", email)
                .param("provider", "OneDrive").with(csrf())).andReturn());
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
                .param("provider", "OneDrive").with(csrf())).andReturn());

        assertEquals("User not found", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveInformation_GetOneDriveInformation_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive")).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(any(), any())).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get("/drive-information")
                        .param("email", email)
                        .param("provider", "OneDrive").with(csrf()))
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

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "random")).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        mockMvc.perform(get("/drive-information")
                        .param("email", email)
                        .param("provider", "random").with(csrf()))
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

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive")).thenReturn(cloudPlatform);
        when(driveInformationService.listAllItemsInOneDrive(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedJsonNode);

        //when
        MvcResult mvcResult = mockMvc.perform(get("/drive-items")
                        .param("email", email)
                        .param("provider", "OneDrive").with(csrf()))
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

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive")).thenReturn(null);

        //when
        ServletException exception = assertThrows(ServletException.class, () -> mockMvc.perform(get("/drive-items")
                .param("email", email)
                .param("provider", "OneDrive").with(csrf())).andReturn());
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
                .param("provider", "OneDrive").with(csrf())).andReturn());

        assertEquals("User not found", exception.getRootCause().getMessage());
    }

    @Test
    @WithMockUser
    void getUserDriveFiles_GetOneDriveInformation_ReturnsBadRequest() throws Exception {
        //given
        UserEntity userEntity = generateUserEntityEnabled();
        String email = userEntity.getEmail();
        CloudPlatform cloudPlatform = generateCloudPlatformEncryptedTokens();

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "OneDrive")).thenReturn(cloudPlatform);
        when(driveInformationService.listAllItemsInOneDrive(any(), any())).thenThrow(new RuntimeException("Drive not found"));

        //when
        mockMvc.perform(get("/drive-items")
                        .param("email", email)
                        .param("provider", "OneDrive").with(csrf()))
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

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(userEntity));
        when(cloudPlatformService.getUserCloudPlatform(email, "random")).thenReturn(cloudPlatform);
        when(driveInformationService.getOneDriveInformation(decrypt(cloudPlatform.getAccessToken()), cloudPlatform.getAccessTokenExpiryDate())).thenReturn(expectedDriveInformationReponse);

        //when
        mockMvc.perform(get("/drive-items")
                        .param("email", email)
                        .param("provider", "random").with(csrf()))
                //then
                .andExpect(status().isBadRequest());
    }


}
