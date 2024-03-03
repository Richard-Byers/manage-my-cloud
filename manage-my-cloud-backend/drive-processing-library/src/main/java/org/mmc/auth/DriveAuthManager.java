package org.mmc.auth;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.gmail.Gmail;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.mmc.implementations.UserAccessTokenCredential;

import java.io.IOException;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DriveAuthManager {

    private static final String MANAGE_MY_CLOUD = "Manage My Cloud";

    public static GraphServiceClient<Request> getOneDriveClient(String userAccessToken, Date expiryDate) {

        OffsetDateTime expiryTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        UserAccessTokenCredential userAccessTokenCredential = new UserAccessTokenCredential(userAccessToken, expiryTime);
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(userAccessTokenCredential);

        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }

    public static Drive getGoogleClient(String refreshToken, String accessToken) {

        try {

            if (isGoogleAccessTokenExpired(accessToken)) {
                accessToken = generateNewGoogleAccessToken(refreshToken).getAccessToken();
            }

            // Create a Credential instance with the access token
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            // Create a Drive service
            return new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                    .setApplicationName(MANAGE_MY_CLOUD)
                    .build();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static Gmail getGmailClient(String refreshToken, String accessToken) {

        try {

            if (isGoogleAccessTokenExpired(accessToken)) {
                accessToken = generateNewGoogleAccessToken(refreshToken).getAccessToken();
            }

            // Create a Credential instance with the access token
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

            var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            var jsonFactory = JacksonFactory.getDefaultInstance();

            // Create a Gmail service
            return new Gmail.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(MANAGE_MY_CLOUD)
                    .build();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private static TokenResponse generateNewGoogleAccessToken(String refreshToken) {

        TokenResponse response = new GoogleTokenResponse();

        try {
            String googleCredentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    JacksonFactory.getDefaultInstance(), new StringReader(googleCredentialsJson));
            response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    refreshToken,
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret())
                    .execute();
        } catch (IOException e) {
            System.out.println(e);
        }

        return response;
    }

    private static boolean isGoogleAccessTokenExpired(String accessToken) throws IOException {

        try {

            com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), getHttpRequestInitializer(accessToken))
                    .setApplicationName(MANAGE_MY_CLOUD)
                    .build();

            //Try to request information back about the user, if it fails, the token is expired
            service.about().get().setFields("user").execute();
            return false; // If the code reaches this point, then the access token is still valid
        } catch (Exception e) {
            if (e.getMessage().contains("401 Unauthorized")) {
                return true; // The access token is expired or invalid
            } else {
                throw e; // Some other error occurred
            }
        }
    }

    private static HttpRequestInitializer getHttpRequestInitializer(String accessToken) {
        // Create a Credentials instance with the access token
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null));

        return httpRequest -> credentials.getRequestMetadata().forEach(httpRequest.getHeaders()::put);
    }

}
