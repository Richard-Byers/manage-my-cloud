package org.mmc.drive;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;

import java.io.IOException;
import java.util.Date;

public interface IDriveInformationService {

    DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate);

    DriveInformationReponse getGoogleDriveInformation(String email, String refreshToken);

    JsonNode listAllItemsInOneDrive(String userAccessToken, Date expiryDate) throws JsonProcessingException;

    JsonNode returnItemsToDelete(JsonNode filesInDrive, UserPreferences userPreferences) throws IOException;

    FilesDeletedResponse deleteRecommendedOneDriveFiles(JsonNode filesToDelete, String userAccessToken, Date expiryDate) throws JsonProcessingException;

}
