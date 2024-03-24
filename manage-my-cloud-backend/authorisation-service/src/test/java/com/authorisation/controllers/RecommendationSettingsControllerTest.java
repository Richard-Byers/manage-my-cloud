package com.authorisation.controllers;

import com.authorisation.entities.RecommendationSettings;
import com.authorisation.entities.UserEntity;
import com.authorisation.repositories.RecommendationSettingsRepository;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecommendationSettingsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class RecommendationSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RecommendationSettingsRepository recommendationSettingsRepository;

    private UserEntity testUser;

    @BeforeEach
    public void setup() {
        this.testUser = new UserEntity();
        this.testUser.setEmail("test@example.com");

        RecommendationSettings testSettings = new RecommendationSettings();
        testSettings.setUserEntity(testUser);

        given(userService.findUserByEmail(testUser.getEmail())).willReturn(Optional.of(testUser));
        given(recommendationSettingsRepository.findByUserEntityEmail(testUser.getEmail())).willReturn(testSettings);
    }

    @Test
    void getRecommendationSettings_ValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(get("/get-preferences/{email}", testUser.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    void getRecommendationSettings_UserNotFound_ReturnsNotFound() {
        given(userService.findUserByEmail(testUser.getEmail())).willReturn(Optional.empty());

        RecommendationSettingsController controller = new RecommendationSettingsController(recommendationSettingsRepository, userService);
        assertThrows(RuntimeException.class, () -> controller.getRecommendationSettings(testUser.getEmail()));
    }

    @Test
    void updateRecommendationSettings_ValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(post("/preference-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", testUser.getEmail())
                        .content("{\"deleteVideos\":true,\"deleteImages\":true,\"deleteDocuments\":true,\"deleteEmails\":true,\"deleteItemsCreatedAfterDays\":7,\"deleteItemsNotChangedSinceDays\":7}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateRecommendationSettings_UserNotFound_ReturnsNotFound() {
        given(userService.findUserByEmail(testUser.getEmail())).willReturn(Optional.empty());

        RecommendationSettingsController controller = new RecommendationSettingsController(recommendationSettingsRepository, userService);
        RecommendationSettings newSettings = new RecommendationSettings();
        assertThrows(RuntimeException.class, () -> controller.updateRecommendationSettings(newSettings, testUser.getEmail()));
    }
}
