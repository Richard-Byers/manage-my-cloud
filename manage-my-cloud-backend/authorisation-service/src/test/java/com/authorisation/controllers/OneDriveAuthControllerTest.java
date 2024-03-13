package com.authorisation.controllers;

import com.authorisation.response.OneDriveTokenResponse;
import com.authorisation.services.CloudPlatformService;
import com.authorisation.services.OneDriveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OneDriveAuthController.class)
@ExtendWith(MockitoExtension.class)
class OneDriveAuthControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private OneDriveService oneDriveService;
    @MockBean
    private CloudPlatformService cloudPlatformService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void getAndStoreUserTokensTest() throws Exception {
        String code = "code";
        String email = "email@example.com";
        OneDriveTokenResponse response = new OneDriveTokenResponse();
        response.setAccessToken("access_token");
        response.setRefreshToken("refresh_token");
        response.setTokenType("token_type");
        response.setExpiresIn(3600L);

        when(oneDriveService.getAndStoreUserTokens(code, email)).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(get("/onedrive-store-tokens")
                        .param("code", code)
                        .param("email", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        OneDriveTokenResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OneDriveTokenResponse.class);

        assertEquals(response.getAccessToken(), result.getAccessToken());
        assertEquals(response.getRefreshToken(), result.getRefreshToken());
    }
}