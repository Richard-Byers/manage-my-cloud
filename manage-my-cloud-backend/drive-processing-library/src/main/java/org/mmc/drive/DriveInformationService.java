package org.mmc.drive;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.util.DateTime;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.google.api.services.drive.model.About;
import com.microsoft.graph.serializer.AdditionalDataManager;
import okhttp3.Request;
import org.mmc.Constants;
import org.mmc.implementations.UserAccessTokenCredential;
import org.mmc.response.CustomDriveItem;
import org.mmc.response.DriveInformationReponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

public class DriveInformationService implements IDriveInformationService {

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

        AdditionalDataManager additionalDataManager = drive.owner.user.additionalDataManager();
        String oneDriveEmail;

        if (additionalDataManager.isEmpty()) {
            oneDriveEmail = "No email linked to account.";
        } else {
            oneDriveEmail = additionalDataManager.get("email").getAsString();
        }

        return mapToDriveInformationResponse(drive.owner.user.displayName,
                oneDriveEmail,
                totalGigabytes,
                usedGigabytes);
    }

    private TokenResponse generateNewGoogleAccessToken(String refreshToken) {

        TokenResponse response = new GoogleTokenResponse();

        try {
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(
                            GsonFactory.getDefaultInstance(), new InputStreamReader(getClass().getResourceAsStream(Constants.GOOGLE_CREDENTIALS_FILE_PATH)));
            response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    refreshToken,
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret())
                    .execute();
        } catch (IOException e) {
            System.out.println(e);
        }

        return response;
    }

    public DriveInformationReponse getGoogleDriveInformation(String email, String refreshToken, String accessToken) {

        //Check if access token is expired or not, no point generating new one if one in the DB works
        try {
            if (isGoogleAccessTokenExpired(accessToken)) {
                accessToken = generateNewGoogleAccessToken(refreshToken).getAccessToken();
            }
        } catch (GeneralSecurityException e) {
            System.out.println(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create a Drive service
        com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), getHttpRequestInitializer(accessToken))
                .setApplicationName("Manage My Cloud")
                .build();

        // Create About objects for the storage and user
        About aboutStorage = null;
        About aboutUser = null;
        try {
            aboutStorage = service.about().get().setFields("storageQuota").execute();
            aboutUser = service.about().get().setFields("user").execute();
        } catch (IOException e) {
            System.out.println(e);
        }

        // Get the storage quota
        About.StorageQuota storageQuota = aboutStorage.getStorageQuota();

        //Convert to GB
        Double totalStorageInGigabytes = Double.parseDouble(ZERO_DECIMAL_FORMAT.format(storageQuota.getLimit() / BYTES_TO_GIGABYTES_DOUBLE));
        Double usedStorageInGigabytes = Double.parseDouble(DECIMAL_FORMAT.format(storageQuota.getUsage() / BYTES_TO_GIGABYTES_DOUBLE));

        // Get the full name of the user
        String userName = aboutUser.getUser().getDisplayName();

        return mapToDriveInformationResponse(userName, email, totalStorageInGigabytes, usedStorageInGigabytes);
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
        root.setChildren(Collections.synchronizedList(new ArrayList<>()));

        driveItems.getCurrentPage().parallelStream().forEach(item -> {
            CustomDriveItem customItem = new CustomDriveItem();
            customItem.setName(item.name);
            customItem.setType(item.folder == null ? item.file.mimeType : "Folder");
            customItem.setCreatedDateTime(item.createdDateTime);
            customItem.setWebUrl(item.webUrl);
            customItem.setChildren(Collections.synchronizedList(new ArrayList<>()));

            if (item.folder != null) {
                listAllSubItemsOneDrive(item.id, customItem);
            }

            root.getChildren().add(customItem);
        });

        String jsonString = mapper.writeValueAsString(root);
        return mapper.readTree(jsonString);
    }

    private void listAllSubItemsOneDrive(String itemId, CustomDriveItem parent) {
        DriveItemCollectionPage subItems = graphClient.me().drive().items(itemId).children().buildRequest().get();

        subItems.getCurrentPage().parallelStream().forEach(subItem -> {
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
        });
    }

    private boolean isGoogleAccessTokenExpired(String accessToken) throws GeneralSecurityException, IOException {

        try {

            com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), getHttpRequestInitializer(accessToken))
                    .setApplicationName("Manage My Cloud")
                    .build();

            //Try to request information back about the user, if it fails, the token is expired
            service.about().get().setFields("user").execute();
            return false; // If the code reaches this point, then the access token is still valid
        } catch (Exception e) {
            if (e.getMessage().contains("401 Unauthorized")) {
                return true; // The access token is expired or invalid
            } else {
                throw e; // Some other error occurred
            }
        }
    }

    public JsonNode fetchAllGoogleDriveFiles(String refreshToken, String accessToken) throws IOException {

        //Check if access token is expired or not, no point generating new one if one in the DB works
        try {
            if (isGoogleAccessTokenExpired(accessToken)) {
                accessToken = generateNewGoogleAccessToken(refreshToken).getAccessToken();
            }
        } catch (GeneralSecurityException e) {
            System.out.println(e);
        }

        com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), getHttpRequestInitializer(accessToken))
                .setApplicationName("Manage My Cloud")
                .build();
        CustomDriveItem root = new CustomDriveItem();
        root.setName("root");
        root.setType("Folder");
        root.setChildren(performFetchAllGoogleDriveFiles(service));

        return mapper.valueToTree(root);
    }

    private List<CustomDriveItem> performFetchAllGoogleDriveFiles(com.google.api.services.drive.Drive service) throws IOException {
        List<CustomDriveItem> allFiles = new ArrayList<>();

        // Define the fields included in the response
        String fields = "nextPageToken, files(id, name, mimeType, createdTime, webViewLink)";

        // Make the request, get the fields for each file and ignore folders
        com.google.api.services.drive.Drive.Files.List request = service.files().list().setFields(fields).setQ("mimeType != 'application/vnd.google-apps.folder'");

        FileList fileList;
        do {
            fileList = request.execute();
            for (File file : fileList.getFiles()) {

                // Convert the Google DateTime to Java OffsetDateTime
                DateTime googleDateTime = file.getCreatedTime();
                OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(googleDateTime.getValue()), ZoneId.systemDefault());

                CustomDriveItem customItem = new CustomDriveItem();
                customItem.setName(file.getName());
                customItem.setType(file.getMimeType());
                customItem.setCreatedDateTime(offsetDateTime);
                customItem.setWebUrl(file.getWebViewLink());

                allFiles.add(customItem);
            }
            request.setPageToken(fileList.getNextPageToken());
        } while (fileList.getNextPageToken() != null && fileList.getNextPageToken().length() > 0);

        return allFiles;
    }

    public JsonNode performFetchAllGoogleDriveFilesBreakdown(String accessToken, String refreshToken) throws IOException {

        //Check if access token is expired or not, no point generating new one if one in the DB works
        try {
            if (isGoogleAccessTokenExpired(accessToken)) {
                accessToken = generateNewGoogleAccessToken(refreshToken).getAccessToken();
            }
        } catch (GeneralSecurityException e) {
            System.out.println(e);
        }

        com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), getHttpRequestInitializer(accessToken))
                .setApplicationName("Manage My Cloud")
                .build();

        List<CustomDriveItem> allImages = new ArrayList<>();
        List<CustomDriveItem> allDocuments = new ArrayList<>();
        List<CustomDriveItem> allOthers = new ArrayList<>();
        List<CustomDriveItem> allAudios = new ArrayList<>(); // New list for audio files
        List<CustomDriveItem> allVideos = new ArrayList<>(); // New list for video files
        List<CustomDriveItem> allEbooks = new ArrayList<>(); // New list for ebook files

        // Define the fields included in the response
        String fields = "nextPageToken, files(id, name, mimeType, createdTime, webViewLink)";

        // Make the request, get the fields for each file and ignore folders
        com.google.api.services.drive.Drive.Files.List request = service.files().list().setFields(fields).setQ("mimeType != 'application/vnd.google-apps.folder'");

        FileList fileList;
        do {
            fileList = request.execute();
            for (File file : fileList.getFiles()) {

                // Convert the Google DateTime to Java OffsetDateTime
                DateTime googleDateTime = file.getCreatedTime();
                OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(googleDateTime.getValue()), ZoneId.systemDefault());

                CustomDriveItem customItem = new CustomDriveItem();
                customItem.setName(file.getName());
                customItem.setType(file.getMimeType());
                customItem.setCreatedDateTime(offsetDateTime);
                customItem.setWebUrl(file.getWebViewLink());

                // Categorize the files based on their mime types
                if (file.getMimeType().startsWith("image/")) {
                    allImages.add(customItem);
                } else if (file.getMimeType().equals("application/pdf") || file.getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    allDocuments.add(customItem);
                } else if (file.getMimeType().startsWith("audio/")) {
                    allAudios.add(customItem); // Add audio files to the allAudios list
                } else if (file.getMimeType().startsWith("video/")) {
                    allVideos.add(customItem); // Add video files to the allVideos list
                } else if (file.getMimeType().equals("application/epub+zip")) {
                    allEbooks.add(customItem); // Add ebook files to the allEbooks list
                } else {
                    allOthers.add(customItem);
                }
            }
            request.setPageToken(fileList.getNextPageToken());
        } while (fileList.getNextPageToken() != null && fileList.getNextPageToken().length() > 0);

        Map<String, List<CustomDriveItem>> categorisedFiles = new HashMap<>();
        categorisedFiles.put("Images", allImages);
        categorisedFiles.put("Documents", allDocuments);
        categorisedFiles.put("Others", allOthers);
        categorisedFiles.put("Audios", allAudios);
        categorisedFiles.put("Videos", allVideos);
        categorisedFiles.put("Ebooks", allEbooks);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(calculateFileTypePercentages(categorisedFiles));

        return jsonNode;
    }

    public JsonNode performFetchAllOneDriveFilesBreakdown(String userAccessToken, Date expiryDate) throws JsonProcessingException {
        OffsetDateTime expiryTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        UserAccessTokenCredential userAccessTokenCredential = new UserAccessTokenCredential(userAccessToken, expiryTime);
        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(userAccessTokenCredential);
        this.graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();

        List<CustomDriveItem> allImages = new ArrayList<>();
        List<CustomDriveItem> allDocuments = new ArrayList<>();
        List<CustomDriveItem> allOthers = new ArrayList<>();
        List<CustomDriveItem> allAudios = new ArrayList<>(); // New list for audio files
        List<CustomDriveItem> allVideos = new ArrayList<>(); // New list for video files
        List<CustomDriveItem> allEbooks = new ArrayList<>(); // New list for ebook files

        DriveItemCollectionPage driveItems = graphClient.me().drive().root().children().buildRequest().get();

        CustomDriveItem root = new CustomDriveItem();
        root.setName("root");
        root.setType("Folder");
        root.setChildren(Collections.synchronizedList(new ArrayList<>()));

        driveItems.getCurrentPage().parallelStream().forEach(item -> {
            CustomDriveItem customItem = new CustomDriveItem();
            customItem.setName(item.name);
            customItem.setType(item.folder == null ? getMimeType(item.name) : "Folder");
            customItem.setCreatedDateTime(item.createdDateTime);
            customItem.setWebUrl(item.webUrl);
            customItem.setChildren(Collections.synchronizedList(new ArrayList<>()));

            // Categorize the files based on their mime types
            if (item.file != null) {
                String mimeType = getMimeType(item.name);
                if (mimeType.startsWith("image/")) {
                    allImages.add(customItem);
                } else if (mimeType.equals("application/pdf") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    allDocuments.add(customItem);
                } else if (mimeType.startsWith("audio/")) {
                    allAudios.add(customItem); // Add audio files to the allAudios list
                } else if (mimeType.startsWith("video/")) {
                    allVideos.add(customItem); // Add video files to the allVideos list
                } else if (mimeType.equals("application/epub+zip")) {
                    allEbooks.add(customItem); // Add ebook files to the allEbooks list
                } else {
                    allOthers.add(customItem);
                }
            }

            if (item.folder != null) {
                listAllSubItemsOneDrivePieChart(item.id, customItem, allImages, allDocuments, allOthers, allAudios, allVideos, allEbooks);
            }
        });

        Map<String, List<CustomDriveItem>> categorisedFiles = new HashMap<>();
        categorisedFiles.put("Images", allImages);
        categorisedFiles.put("Documents", allDocuments);
        categorisedFiles.put("Others", allOthers);
        categorisedFiles.put("Audios", allAudios);
        categorisedFiles.put("Videos", allVideos);
        categorisedFiles.put("Ebooks", allEbooks);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.valueToTree(calculateFileTypePercentages(categorisedFiles));

        return jsonNode;
    }

    private void listAllSubItemsOneDrivePieChart(String itemId, CustomDriveItem parent, List<CustomDriveItem> allImages, List<CustomDriveItem> allDocuments, List<CustomDriveItem> allOthers, List<CustomDriveItem> allAudios, List<CustomDriveItem> allVideos, List<CustomDriveItem> allEbooks) {
        DriveItemCollectionPage subItems = graphClient.me().drive().items(itemId).children().buildRequest().get();

        subItems.getCurrentPage().forEach(subItem -> {
            CustomDriveItem customSubItem = new CustomDriveItem();
            customSubItem.setName(subItem.name);
            customSubItem.setType(subItem.folder == null ? getMimeType(subItem.name) : "Folder");
            customSubItem.setCreatedDateTime(subItem.createdDateTime);
            customSubItem.setWebUrl(subItem.webUrl);
            customSubItem.setChildren(new ArrayList<>());

            // Categorize the files based on their mime types
            if (subItem.file != null) {
                String mimeType = getMimeType(subItem.name);
                if (mimeType.startsWith("image/")) {
                    allImages.add(customSubItem);
                } else if (mimeType.equals("application/pdf") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    allDocuments.add(customSubItem);
                } else if (mimeType.startsWith("audio/")) {
                    allAudios.add(customSubItem); // Add audio files to the allAudios list
                } else if (mimeType.startsWith("video/")) {
                    allVideos.add(customSubItem); // Add video files to the allVideos list
                } else if (mimeType.equals("application/epub+zip")) {
                    allEbooks.add(customSubItem); // Add ebook files to the allEbooks list
                } else {
                    allOthers.add(customSubItem);
                }
            }

            if (subItem.folder != null) {
                listAllSubItemsOneDrivePieChart(subItem.id, customSubItem, allImages, allDocuments, allOthers, allAudios, allVideos, allEbooks);
            }

            parent.getChildren().add(customSubItem);
        });
    }

    private String getMimeType(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        switch(extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pdf":
                return "application/pdf";
            case "mov":
                return "video/mov";
            case "mp4":
                return "video/mp4";
            case "mp3":
                return "audio/mp3";
            case "wav":
                return "audio/wav";
            case "flac":
                return "audio/flac";
            case "epub":
                return "application/epub+zip";
            default:
                return "application/octet-stream";
        }
    }

    private Map<String, Double> calculateFileTypePercentages(Map<String, List<CustomDriveItem>> categorisedFiles) {
        int totalFiles = categorisedFiles.get("Images").size() + categorisedFiles.get("Documents").size() + categorisedFiles.get("Others").size() + categorisedFiles.get("Audios").size() + categorisedFiles.get("Videos").size() + categorisedFiles.get("Ebooks").size();

        DecimalFormat df = new DecimalFormat("#.##"); // 2 decimal places

        double imagePercentage = Double.parseDouble(df.format(((double) categorisedFiles.get("Images").size() / totalFiles) * 100));
        double documentPercentage = Double.parseDouble(df.format(((double) categorisedFiles.get("Documents").size() / totalFiles) * 100));
        double otherPercentage = Double.parseDouble(df.format(((double) categorisedFiles.get("Others").size() / totalFiles) * 100));
        double audioPercentage = Double.parseDouble(df.format(((double) categorisedFiles.get("Audios").size() / totalFiles) * 100)); // Calculate the percentage of audio files
        double videoPercentage = Double.parseDouble(df.format(((double) categorisedFiles.get("Videos").size() / totalFiles) * 100)); // Calculate the percentage of video files
        double ebookPercentage = Double.parseDouble(df.format(((double) categorisedFiles.get("Ebooks").size() / totalFiles) * 100)); // Calculate the percentage of ebook files

        Map<String, Double> fileTypePercentages = new HashMap<>();
        fileTypePercentages.put("Images", imagePercentage);
        fileTypePercentages.put("Documents", documentPercentage);
        fileTypePercentages.put("Others", otherPercentage);
        fileTypePercentages.put("Audio", audioPercentage);
        fileTypePercentages.put("Video", videoPercentage);
        fileTypePercentages.put("Ebooks", ebookPercentage);

        return fileTypePercentages;
    }

    private HttpRequestInitializer getHttpRequestInitializer(String accessToken) {
        // Create a Credentials instance with the access token
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null));

        return httpRequest -> credentials.getRequestMetadata().forEach(httpRequest.getHeaders()::put);
    }

    public DriveInformationReponse mapToDriveInformationResponse(String displayName, String email, Double total, Double used) {

        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(displayName);
        driveInformationReponse.setEmail(email);
        driveInformationReponse.setTotal(total);
        driveInformationReponse.setUsed(used);

        return driveInformationReponse;
    }
}
