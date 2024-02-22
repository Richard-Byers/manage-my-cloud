package com.authorisation.controllers;


import com.authorisation.services.GoogleAuthService;
import com.authorisation.services.OneDriveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UnlinkAccountController.class)
@ExtendWith(MockitoExtension.class)
class UnlinkAccountControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private OneDriveService oneDriveService;

    @MockBean
    private GoogleAuthService googleAuthService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void unlinkAccountTest() throws Exception {
        String email = "email@example.com";
        String driveEmail = "email2@example.com";

        doNothing().when(oneDriveService).unlinkOneDrive(email, driveEmail);

        mockMvc.perform(delete("/unlink-drive")
                        .param("email", email)
                        .param("provider", "OneDrive").with(csrf()))
                .andExpect(status().isOk());

        verify(oneDriveService).unlinkOneDrive(email, driveEmail);
    }
}