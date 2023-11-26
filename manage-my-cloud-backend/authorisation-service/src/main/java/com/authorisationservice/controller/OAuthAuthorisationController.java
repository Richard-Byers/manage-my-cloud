package com.authorisationservice.controller;

import com.authorisationservice.model.DriveQuickStart;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class OAuthAuthorisationController {

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

    @GetMapping("/oauth2/authorize/google")
    private RedirectView getDashboard() throws GeneralSecurityException, IOException {
            return new RedirectView("http://localhost:3000/dashboard");
    }

    @GetMapping("/getDetails")
    public Map<String, Object> getAbout() throws IOException, GeneralSecurityException {
        Map<String, Object> response = new HashMap<>();
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = new Drive.Builder(HTTP_TRANSPORT, DriveQuickStart.JSON_FACTORY, DriveQuickStart.getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(DriveQuickStart.APPLICATION_NAME)
                    .build();

            About about = service.about().get().setFields("*").execute();
            response.put("about", about);

        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            response.put("error", "Error: " + e.getMessage());
        }
        return response;
    }
}
