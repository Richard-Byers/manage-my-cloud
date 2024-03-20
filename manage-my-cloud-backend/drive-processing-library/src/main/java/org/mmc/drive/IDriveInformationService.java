package org.mmc.drive;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.util.Date;

public interface IDriveInformationService {

    DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate);

    DriveInformationReponse getGoogleDriveInformation(String email, String refreshToken, String accessToken) throws IOException;

    JsonNode listAllItemsInOneDrive(String userAccessToken, Date expiryDate, SimpMessagingTemplate simpMessagingTemplate, String email) throws JsonProcessingException;

    JsonNode fetchAllGoogleDriveFiles(String refreshToken, String accessToken, SimpMessagingTemplate simpMessagingTemplate, String email, boolean isGmail) throws IOException;

    DriveInformationReponse mapToDriveInformationResponse(String displayName, String email, Double total, Double used);

    JsonNode returnItemsToDelete(JsonNode filesInDrive, UserPreferences userPreferences, SimpMessagingTemplate simpMessagingTemplate, String email) throws IOException;

    FilesDeletedResponse deleteRecommendedOneDriveFiles(JsonNode filesToDelete, String userAccessToken, Date expiryDate, SimpMessagingTemplate simpMessagingTemplate, String email) throws JsonProcessingException;

}
