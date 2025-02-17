package com.authorisation.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.io.StringReader;

public class GoogleTokenService {

    public static GoogleTokenResponse getGoogleTokenResponse(String authCodeOutput, String googleCredentialsJson) throws IOException {

        String redirectUri = System.getenv("GOOGLE_REDIRECT_URI") != null ? System.getenv("GOOGLE_REDIRECT_URI") : "http://localhost:3000";

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new StringReader(googleCredentialsJson));
        return new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                "https://www.googleapis.com/oauth2/v4/token",
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                authCodeOutput,
                redirectUri)
                .execute();
    }
}
