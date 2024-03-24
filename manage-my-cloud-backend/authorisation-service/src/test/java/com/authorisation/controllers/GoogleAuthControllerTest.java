package com.authorisation.controllers;

import com.authorisation.config.UserAuthenticationProvider;
import com.authorisation.dto.UserDto;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.GoogleAuthService;
import com.authorisation.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GoogleAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GoogleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserAuthenticationProvider userAuthenticationProvider;

    @MockBean
    private GoogleAuthService googleAuthService;

    @Test
    public void whenValidInputThenReturns200() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@gmail.com");

        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setToken("token");

        when(userService.registerGoogleUser(anyString(), anyString(), anyString(), anyString())).thenReturn(userEntity);
        when(userService.googleLogin(anyString())).thenReturn(userDto);
        when(userAuthenticationProvider.createToken(anyString())).thenReturn("token");

        when(googleAuthService.storeAuthCode(anyString())).thenReturn(ResponseEntity.ok(userDto));

        String authCodeJson = "{\"authCode\":\"4/0AfJohXnSbVBJQR7PG35P1gHkn5KYEWALPjQ5U2zx_9wXLDxODxq6tAnyyKRBkZi4xZ9NBQ\"}";

        mockMvc.perform(post("/registergoogleuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeJson))
                .andExpect(status().isOk());

        String email = "test@gmail.com";
        mockMvc.perform(post("/link-google-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authCodeJson)
                        .param("email", email))
                .andExpect(status().isOk());
    }
}