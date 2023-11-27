package com.authorisationservice.controller;

import com.authorisationservice.model.DriveQuickStart;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REDIRECT_URI;




@CrossOrigin(origins = "*")
@RestController
public class OAuthAuthorisationController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthorisationController.class);


    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

//    @GetMapping("/oauth/authorise")
//    private Map<String, Object> getDetails() throws IOException, GeneralSecurityException {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            Drive service = new Drive.Builder(HTTP_TRANSPORT, DriveQuickStart.JSON_FACTORY, DriveQuickStart.getCredentials(HTTP_TRANSPORT))
//                    .setApplicationName(DriveQuickStart.APPLICATION_NAME)
//                    .build();
//
//            FileList result = service.files().list()
//                    .setPageSize(10)
//                    .setFields("nextPageToken, files(id, name)")
//                    .execute();
//            List<File> files = result.getFiles();
//            if (files == null || files.isEmpty()) {
//                response.put("message", "No files found.");
//            } else {
//                List<Map<String, String>> fileList = new ArrayList<>();
//                for (File file : files) {
//                    Map<String, String> fileData = new HashMap<>();
//                    fileData.put("name", file.getName());
//                    fileData.put("id", file.getId());
//                    fileList.add(fileData);
//                }
//                response.put("files", fileList);
//            }
//        } catch (Exception e) {
//            // Log the exception
//            e.printStackTrace();
//            response.put("error", "Error: " + e.getMessage());
//        }
//        return response;
//    }

    @GetMapping("/csrf-token-endpoint")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return csrfToken.getToken();
    }

    @PostMapping("/storeauthcode")
    public void storeAuthCode(@RequestHeader("X-Requested-With") String requestedWith, @RequestBody String authCode) throws IOException, GeneralSecurityException {
        if (requestedWith == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid request header");

        }

        try {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new InputStreamReader(getClass().getResourceAsStream(CREDENTIALS_FILE_PATH)));
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        authCode,
                        "http://localhost:8080/login/oauth2/code/google")
                        .execute();
            String accessToken = tokenResponse.getAccessToken();
        } catch (Exception e) {
            // Log the exception
            System.out.println(e);
        }
    }

//    public void getAccessToken(String authCode) throws IOException, GeneralSecurityException {
//
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(
//                        JacksonFactory.getDefaultInstance(), new FileReader(CREDENTIALS_FILE_PATH));
//        GoogleTokenResponse tokenResponse =
//                new GoogleAuthorizationCodeTokenRequest(
//                        HTTP_TRANSPORT,
//                        JacksonFactory.getDefaultInstance(),
//                        "https://oauth2.googleapis.com/token",
//                        clientSecrets.getDetails().getClientId(),
//                        clientSecrets.getDetails().getClientSecret(),
//                        authCode,
//                        REDIRECT_URI)  // Specify the same redirect URI that you use with your web
//                        // app. If you don't have a web version of your app, you can
//                        // specify an empty string.
//                        .execute();
//
//        String accessToken = tokenResponse.getAccessToken();
//        logger.info("Access Token: " + accessToken);
//    }

//    @GetMapping("/oauth2/authorize/google")
//    private RedirectView getDashboard() throws GeneralSecurityException, IOException {
//            return new RedirectView("http://localhost:3000/dashboard");
//    }

    @GetMapping("/getDetails")
    public Map<String, Object> getAbout(GoogleCredential credential) throws IOException, GeneralSecurityException {
        Map<String, Object> response = new HashMap<>();
        try {
            Drive drive =
                    new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                            .setApplicationName("Auth Code Exchange Demo")
                            .build();

            About about = drive.about().get().setFields("*").execute();
            response.put("about", about);

        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            response.put("error", "Error: " + e.getMessage());
        }
        return response;
    }
}
