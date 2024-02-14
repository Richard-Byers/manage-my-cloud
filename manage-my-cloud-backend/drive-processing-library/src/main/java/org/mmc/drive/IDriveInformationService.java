package org.mmc.drive;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.mmc.response.DriveInformationReponse;

import java.io.IOException;
import java.util.Date;

public interface IDriveInformationService {

    DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate);
    DriveInformationReponse getGoogleDriveInformation(String email, String refreshToken, String accessToken);

    JsonNode listAllItemsInOneDrive(String userAccessToken, Date expiryDate) throws JsonProcessingException;
    JsonNode fetchAllGoogleDriveFiles(String refreshToken, String accessToken) throws IOException;
    DriveInformationReponse mapToDriveInformationResponse(String displayName, String email, Double total, Double used);

}
