package org.mmc.drive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.mmc.implementations.UserAccessTokenCredential;
import org.mmc.response.CustomDriveItem;
import org.mmc.response.DriveInformationReponse;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class DriveInformationService implements IDriveInformationService {

    private static final int BYTES_TO_GIGABYTES = 1073741824;
    private static final double BYTES_TO_GIGABYTES_DOUBLE = 1073741824.0;
    private GraphServiceClient<Request> graphClient;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat ZERO_DECIMAL_FORMAT = new DecimalFormat("#");
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

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
        Double totalGigabytes = Double.parseDouble(ZERO_DECIMAL_FORMAT.format(drive.quota.total / BYTES_TO_GIGABYTES_DOUBLE));
        Double usedGigabytes = Double.parseDouble(DECIMAL_FORMAT.format(drive.quota.used / BYTES_TO_GIGABYTES_DOUBLE));

        return mapToDriveInformationResponse(drive.owner.user.displayName,
                drive.driveType,
                totalGigabytes,
                usedGigabytes);
    }

    public JsonNode listAllItemsInOneDrive(String userAccessToken, Date expiryDate) throws JsonProcessingException {
        OffsetDateTime expiryTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        UserAccessTokenCredential userAccessTokenCredential = new UserAccessTokenCredential(userAccessToken, expiryTime);
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(userAccessTokenCredential);
        this.graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();

        DriveItemCollectionPage driveItems = graphClient.me().drive().root().children().buildRequest().get();

        CustomDriveItem root = new CustomDriveItem();
        root.setName("root");
        root.setType("Folder");
        root.setChildren(new ArrayList<>());

        for (DriveItem item : driveItems.getCurrentPage()) {
            CustomDriveItem customItem = new CustomDriveItem();
            customItem.setName(item.name);
            customItem.setType(item.folder == null ? item.file.mimeType : "Folder");
            customItem.setCreatedDateTime(item.createdDateTime);
            customItem.setWebUrl(item.webUrl);
            customItem.setChildren(new ArrayList<>());

            if (item.folder != null) {
                listAllSubItemsOneDrive(item.id, customItem);
            }

            root.getChildren().add(customItem);
        }

        String jsonString = mapper.writeValueAsString(root);
        return mapper.readTree(jsonString);
    }

    private void listAllSubItemsOneDrive(String itemId, CustomDriveItem parent) {
        DriveItemCollectionPage subItems = graphClient.me().drive().items(itemId).children().buildRequest().get();

        for (DriveItem subItem : subItems.getCurrentPage()) {
            CustomDriveItem customSubItem = new CustomDriveItem();
            customSubItem.setName(subItem.name);
            customSubItem.setType(subItem.folder == null ? subItem.file.mimeType : "Folder");
            customSubItem.setCreatedDateTime(subItem.createdDateTime);
            customSubItem.setWebUrl(subItem.webUrl);
            customSubItem.setChildren(new ArrayList<>());

            if (subItem.folder != null) {
                listAllSubItemsOneDrive(subItem.id, customSubItem);
            }

            parent.getChildren().add(customSubItem);
        }
    }

    public DriveInformationReponse mapToDriveInformationResponse(String displayName, String driveType, Double total, Double used) {

        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(displayName);
        driveInformationReponse.setDriveType(driveType);
        driveInformationReponse.setTotal(total);
        driveInformationReponse.setUsed(used);

        return driveInformationReponse;
    }

}
