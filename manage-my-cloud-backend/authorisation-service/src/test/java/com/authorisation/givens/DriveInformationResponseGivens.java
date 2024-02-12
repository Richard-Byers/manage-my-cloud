package com.authorisation.givens;

import org.mmc.response.DriveInformationReponse;

public class DriveInformationResponseGivens {

    public static DriveInformationReponse generateDriveInformationResponse(){
        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setTotal(100.0);
        driveInformationReponse.setUsed(50.0);
        return driveInformationReponse;
    }

}
