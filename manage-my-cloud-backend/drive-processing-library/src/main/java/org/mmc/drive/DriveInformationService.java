package org.mmc.drive;

import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.mmc.implementations.UserAccessTokenCredential;
import org.mmc.response.DriveInformationReponse;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DriveInformationService implements IDriveInformationService {

    private static final int BYTES_TO_GIGABYTES = 1073741824;
    private static final double BYTES_TO_GIGABYTES_DOUBLE = 1073741824.0;
    private GraphServiceClient<Request> graphClient;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    public DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate) {

        OffsetDateTime expiryTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        UserAccessTokenCredential userAccessTokenCredential = new UserAccessTokenCredential(userAccessToken, expiryTime);
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(userAccessTokenCredential);
        this.graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();

        Drive drive = graphClient.me().drive().buildRequest().get();

        if (drive == null) {
            throw new RuntimeException("Drive not found");
        }

        // Convert to GB
        Long totalGigabytes = drive.quota.total / BYTES_TO_GIGABYTES;
        Double usedGigabytes = Double.parseDouble(DECIMAL_FORMAT.format(drive.quota.used / BYTES_TO_GIGABYTES_DOUBLE));

        return mapToDriveInformationResponse(drive.owner.user.displayName,
                drive.driveType,
                totalGigabytes,
                usedGigabytes);
    }

    public DriveInformationReponse mapToDriveInformationResponse(String displayName, String driveType, Long total, Double used) {

        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(displayName);
        driveInformationReponse.setDriveType(driveType);
        driveInformationReponse.setTotal(total);
        driveInformationReponse.setUsed(used);

        return driveInformationReponse;
    }

}
