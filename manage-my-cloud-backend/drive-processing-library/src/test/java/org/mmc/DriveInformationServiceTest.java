package org.mmc;

import org.junit.Test;
import org.mmc.drive.DriveInformationService;

import java.util.Date;

public class DriveInformationServiceTest {

    private final DriveInformationService driveInformationService = new DriveInformationService();

    @Test
    void getOneDriveInformation_ReturnsDriveInformation() {
        //given
        String userAccessToken = "token";
        Date expiryDate = new Date();

        //when
        driveInformationService.getOneDriveInformation(userAccessToken, expiryDate);

        //then

    }

}
