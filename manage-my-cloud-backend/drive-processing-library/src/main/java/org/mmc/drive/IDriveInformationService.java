package org.mmc.drive;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.mmc.response.DriveInformationReponse;

import java.util.Date;

public interface IDriveInformationService {

    DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate);

    JsonNode listAllItemsInOneDrive(String userAccessToken, Date expiryDate) throws JsonProcessingException;

}
