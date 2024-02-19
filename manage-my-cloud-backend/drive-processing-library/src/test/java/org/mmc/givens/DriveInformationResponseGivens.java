package org.mmc.givens;

import com.google.api.services.drive.model.About;
import com.microsoft.graph.models.Drive;
import org.mmc.response.DriveInformationReponse;

import java.text.DecimalFormat;

public class DriveInformationResponseGivens {

    private static final double BYTES_TO_GIGABYTES_DOUBLE = 1073741824.0;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat ZERO_DECIMAL_FORMAT = new DecimalFormat("#");

    public static DriveInformationReponse generateDriveInformationResponseNoEmail(Drive drive) {
        Double totalGigabytes = Double.parseDouble(ZERO_DECIMAL_FORMAT.format(drive.quota.total / BYTES_TO_GIGABYTES_DOUBLE));
        Double usedGigabytes = Double.parseDouble(DECIMAL_FORMAT.format(drive.quota.used / BYTES_TO_GIGABYTES_DOUBLE));
        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(drive.owner.user.displayName);
        driveInformationReponse.setEmail("No email linked to account.");
        driveInformationReponse.setUsed(usedGigabytes);
        driveInformationReponse.setTotal(totalGigabytes);
        return driveInformationReponse;
    }

    public static DriveInformationReponse generateDriveInformationResponseWithEmail(Drive drive) {
        Double totalGigabytes = Double.parseDouble(ZERO_DECIMAL_FORMAT.format(drive.quota.total / BYTES_TO_GIGABYTES_DOUBLE));
        Double usedGigabytes = Double.parseDouble(DECIMAL_FORMAT.format(drive.quota.used / BYTES_TO_GIGABYTES_DOUBLE));
        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(drive.owner.user.displayName);
        driveInformationReponse.setEmail(drive.owner.user.additionalDataManager().get("email").getAsString());
        driveInformationReponse.setUsed(usedGigabytes);
        driveInformationReponse.setTotal(totalGigabytes);
        return driveInformationReponse;
    }

    public static DriveInformationReponse generateGoogleDriveInformationResponseWithEmail(About about, String email) {
        About.StorageQuota storageQuota = about.getStorageQuota();
        String userName = about.getUser().getDisplayName();

        Double totalGigabytes = Double.parseDouble(ZERO_DECIMAL_FORMAT.format(storageQuota.getLimit() / BYTES_TO_GIGABYTES_DOUBLE));
        Double usedGigabytes = Double.parseDouble(DECIMAL_FORMAT.format(storageQuota.getUsage() / BYTES_TO_GIGABYTES_DOUBLE));
        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(userName);
        driveInformationReponse.setEmail(email);
        driveInformationReponse.setUsed(usedGigabytes);
        driveInformationReponse.setTotal(totalGigabytes);
        return driveInformationReponse;
    }

}
