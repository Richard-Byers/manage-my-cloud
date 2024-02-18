package org.mmc.auth;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.mmc.Constants;
import org.mmc.implementations.UserAccessTokenCredential;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DriveAuthManager {

    public static GraphServiceClient<Request> getOneDriveClient(String userAccessToken, Date expiryDate) {

        OffsetDateTime expiryTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        UserAccessTokenCredential userAccessTokenCredential = new UserAccessTokenCredential(userAccessToken, expiryTime);
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(userAccessTokenCredential);

        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }

    public static Drive getGoogleClient(String refreshToken) {

        TokenResponse response;
        try {
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(
                            JacksonFactory.getDefaultInstance(), new InputStreamReader(DriveAuthManager.class.getResourceAsStream(Constants.GOOGLE_CREDENTIALS_FILE_PATH)));
            response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(),
                    new JacksonFactory(),
                    refreshToken,
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret())
                    .execute();

            // Create a Credential instance with the access token
            GoogleCredential credential = new GoogleCredential().setAccessToken(response.getAccessToken());

            // Create a Drive service
            return new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                    .setApplicationName("Manage My Cloud")
                    .build();
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

}
