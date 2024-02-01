package org.mmc.drive;


import org.mmc.response.DriveInformationReponse;

import java.util.Date;

public interface IDriveInformationService {

    DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate);

}
