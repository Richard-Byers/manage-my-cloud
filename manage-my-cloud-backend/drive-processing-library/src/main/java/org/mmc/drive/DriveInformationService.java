package org.mmc.drive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.serializer.AdditionalDataManager;
import okhttp3.Request;
import org.mmc.pojo.CustomEmail;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mmc.Constants;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.CustomDriveItem;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;
import org.mmc.util.JsonUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mmc.auth.DriveAuthManager.*;
import static org.mmc.enumerations.ItemTypeChecker.DocumentType.isDocumentType;
import static org.mmc.enumerations.ItemTypeChecker.ImageType.isImageType;
import static org.mmc.enumerations.ItemTypeChecker.OtherType.isOtherType;
import static org.mmc.enumerations.ItemTypeChecker.VideoType.isVideoType;

public class DriveInformationService implements IDriveInformationService {

    private static final double BYTES_TO_GIGABYTES_DOUBLE = 1073741824.0;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat ZERO_DECIMAL_FORMAT = new DecimalFormat("#");
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private GraphServiceClient<Request> graphClient;

    public DriveInformationReponse getOneDriveInformation(String userAccessToken, Date expiryDate) {

        this.graphClient = getOneDriveClient(userAccessToken, expiryDate);

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

    public String getOneDriveEmail(String userAccessToken, Date expiryDate) {

        this.graphClient = getOneDriveClient(userAccessToken, expiryDate);

        User user = graphClient.me().buildRequest().get();

        if (user == null) {
            throw new RuntimeException("Drive not found");
        }

        return user.mail;
    }

    public String getGoogleDriveEmail(String refreshToken, String accessToken) throws IOException {

        com.google.api.services.drive.Drive service = getGoogleClient(refreshToken, accessToken);

        if (service == null) {
            throw new RuntimeException("Failed to create google client");
        }

        About user = service.about().get().setFields("user").execute();

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user.getUser().getEmailAddress();
    }

    public DriveInformationReponse getGoogleDriveInformation(String email, String refreshToken, String accessToken) {

        //Check if access token is expired or not, no point generating new one if one in the DB works
        com.google.api.services.drive.Drive service = getGoogleClient(refreshToken, accessToken);

        // Create About objects for the storage and user
        About aboutStorage = null;
        About aboutUser = null;
        try {
            aboutStorage = service.about().get().setFields("storageQuota").execute();
            aboutUser = service.about().get().setFields("user").execute();
        } catch (Exception e) {
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

    public JsonNode listAllItemsInOneDrive(String userAccessToken, Date expiryDate, SimpMessagingTemplate simpMessagingTemplate, String email) throws JsonProcessingException {
        this.graphClient = getOneDriveClient(userAccessToken, expiryDate);

        DriveItemCollectionPage driveItems = graphClient.me().drive().root().children().buildRequest().get();

        CustomDriveItem root = new CustomDriveItem();
        root.setName("root");
        root.setType("Folder");
        root.setChildren(Collections.synchronizedList(new ArrayList<>()));
        AtomicInteger totalItemCount = new AtomicInteger();
        AtomicInteger itemsProcessed = new AtomicInteger();

        while (driveItems != null) {
            totalItemCount.getAndAdd(driveItems.getCurrentPage().size());

            if (driveItems.getNextPage() != null) {
                driveItems = driveItems.getNextPage().buildRequest().get();
            } else {
                driveItems = null;
            }
        }

        driveItems = graphClient.me().drive().root().children().buildRequest().get();

        while (driveItems != null) {
            driveItems.getCurrentPage().parallelStream().forEach(item -> {
                CustomDriveItem customItem = new CustomDriveItem();
                customItem.setId(item.id);
                customItem.setName(item.name);
                if (item.folder == null && item.file != null) {
                    customItem.setType(item.file.mimeType);
                } else if (item.folder != null) {
                    customItem.setType("Folder");
                } else {
                    customItem.setType("Unknown");
                }
                customItem.setLastModifiedDateTime(item.lastModifiedDateTime);
                customItem.setCreatedDateTime(item.createdDateTime);
                customItem.setWebUrl(item.webUrl);
                customItem.setChildren(Collections.synchronizedList(new ArrayList<>()));

                if (item.folder != null) {
                    listAllSubItemsOneDrive(item.id, root);
                }

                root.getChildren().add(customItem);
                itemsProcessed.getAndIncrement();
                simpMessagingTemplate.convertAndSendToUser(email, "/queue/progress", itemsProcessed.get() * 100 / totalItemCount.get());
            });

            if (driveItems.getNextPage() != null) {
                driveItems = driveItems.getNextPage().buildRequest().get();
            } else {
                driveItems = null;
            }
        }

        String jsonString = mapper.writeValueAsString(root);
        return mapper.readTree(jsonString);
    }

    public JsonNode returnItemsToDelete(JsonNode filesInDrive, UserPreferences userPreferences, SimpMessagingTemplate simpMessagingTemplate, String email) throws IOException {
        AtomicInteger totalItemCount = new AtomicInteger();
        CustomDriveItem filesInUserDrive = mapper.treeToValue(filesInDrive, CustomDriveItem.class);
        if (filesInUserDrive.getEmails() != null) {
            totalItemCount.getAndAdd(filesInUserDrive.getEmails().size());
        }
        if (filesInUserDrive.getChildren() != null) {
            totalItemCount.getAndAdd(filesInUserDrive.getChildren().size());
        }
        AtomicInteger itemsProcessed = new AtomicInteger();

        boolean recommendVideos = userPreferences.isDeleteVideos();
        boolean recommendImages = userPreferences.isDeleteImages();
        boolean recommendDocuments = userPreferences.isDeleteDocuments();
        boolean recommendEmails = userPreferences.isDeleteEmails();
        int recommendItemsCreatedDaysAgo = userPreferences.getDeleteItemsCreatedAfterDays();
        int recommendItemsNotChangedDaysAgo = userPreferences.getDeleteItemsNotChangedSinceDays();
        int recommendEmailsAfterDays = userPreferences.getDeleteEmailsAfterDays();

        CustomDriveItem recommendations = new CustomDriveItem();
        recommendations.setName("root");
        recommendations.setType("Folder");
        recommendations.setChildren(Collections.synchronizedList(new ArrayList<>()));
        recommendations.setEmails(Collections.synchronizedList(new ArrayList<>()));

        try {
            // Iterate through files in drive
            filesInUserDrive.getChildren().parallelStream().forEach(item -> {
                processItem(item, recommendations, recommendImages, recommendVideos, recommendDocuments, recommendItemsCreatedDaysAgo, recommendItemsNotChangedDaysAgo);
                itemsProcessed.getAndIncrement();
                simpMessagingTemplate.convertAndSendToUser(email, "/queue/recommendation-progress", itemsProcessed.get() * 100 / totalItemCount.get());
            });

            // Iterate through Emails
            if (recommendEmails && filesInUserDrive.getEmails() != null) {
                filesInUserDrive.getEmails().parallelStream().forEach(item -> {
                    processEmail(item, recommendations, recommendEmailsAfterDays);
                    itemsProcessed.getAndIncrement();
                    simpMessagingTemplate.convertAndSendToUser(email, "/queue/recommendation-progress", itemsProcessed.get() * 100 / totalItemCount.get());
                });
            }

            return mapper.readTree(mapper.writeValueAsString(recommendations));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processItem(CustomDriveItem item, CustomDriveItem recommendations, boolean recommendImages, boolean recommendVideos, boolean recommendDocuments, int recommendItemsCreatedDaysAgo, int recommendItemsNotChangedDaysAgo) {
        if (!Objects.equals(item.getType(), "Folder")) {
            String itemName = item.getName();
            OffsetDateTime createdDateTime = item.getCreatedDateTime();
            OffsetDateTime lastModifiedDateTime = item.getLastModifiedDateTime();
            if (recommendImages && isImageType(itemName) && compareDates(createdDateTime, lastModifiedDateTime, recommendItemsCreatedDaysAgo, recommendItemsNotChangedDaysAgo)) {
                recommendations.getChildren().add(item);
            } else if (recommendVideos && isVideoType(itemName) && compareDates(createdDateTime, lastModifiedDateTime, recommendItemsCreatedDaysAgo, recommendItemsNotChangedDaysAgo)) {
                recommendations.getChildren().add(item);
            } else if (recommendDocuments && isDocumentType(itemName) && compareDates(createdDateTime, lastModifiedDateTime, recommendItemsCreatedDaysAgo, recommendItemsNotChangedDaysAgo)) {
                recommendations.getChildren().add(item);
            } else if (isOtherType(itemName) && compareDates(createdDateTime, lastModifiedDateTime, recommendItemsCreatedDaysAgo, recommendItemsNotChangedDaysAgo)) {
                recommendations.getChildren().add(item);
            }
        }
    }

    private void processEmail(CustomEmail item, CustomDriveItem recommendations, int deleteEmailsAfterDays) {
        if (compareDatesOneDate(item.getReceivedDate(), deleteEmailsAfterDays)) {
            recommendations.getEmails().add(item);
        }
    }

    public FilesDeletedResponse deleteRecommendedOneDriveFiles(JsonNode filesToDelete,
                                                               String userAccessToken,
                                                               Date expiryDate,
                                                               SimpMessagingTemplate simpMessagingTemplate,
                                                               String email) throws JsonProcessingException {
        this.graphClient = getOneDriveClient(userAccessToken, expiryDate);
        CustomDriveItem filesInUserDrive = mapper.treeToValue(filesToDelete, CustomDriveItem.class);
        AtomicInteger filesDeleted = new AtomicInteger();
        AtomicInteger totalItemCount = new AtomicInteger();

        if (filesInUserDrive.getChildren() != null) {
            totalItemCount.getAndAdd(filesInUserDrive.getChildren().size());
        }

        filesInUserDrive.getChildren().parallelStream().forEach(item -> {
            try {
                graphClient.me().drive().items(item.getId()).buildRequest().delete();
                filesDeleted.getAndIncrement();
                simpMessagingTemplate.convertAndSendToUser(email, "/queue/deletion-progress", filesDeleted.get() * 100 / totalItemCount.get());
            } catch (Exception e) {
                throw new RuntimeException("Error deleting OneDrive files");
            }
        });
        FilesDeletedResponse filesDeletedResponse = new FilesDeletedResponse();
        filesDeletedResponse.setFilesDeleted(filesDeleted.get());
        return filesDeletedResponse;
    }

    private void listAllSubItemsOneDrive(String itemId, CustomDriveItem root) {
        DriveItemCollectionPage subItems = graphClient.me().drive().items(itemId).children().buildRequest().get();

        if (subItems == null) {
            return;
        }

        subItems.getCurrentPage().parallelStream().forEach(subItem -> {
            CustomDriveItem customSubItem = new CustomDriveItem();
            customSubItem.setId(subItem.id);
            customSubItem.setName(subItem.name);
            customSubItem.setType(subItem.folder == null ? subItem.file.mimeType : "Folder");
            customSubItem.setLastModifiedDateTime(subItem.lastModifiedDateTime);
            customSubItem.setCreatedDateTime(subItem.createdDateTime);
            customSubItem.setWebUrl(subItem.webUrl);
            customSubItem.setChildren(new ArrayList<>());

            if (subItem.folder != null) {
                listAllSubItemsOneDrive(subItem.id, root);
            }

            root.getChildren().add(customSubItem);
        });
    }

    //Helper Methods

    public JsonNode fetchAllGoogleDriveFiles(String refreshToken, String accessToken, SimpMessagingTemplate simpMessagingTemplate, String email) throws IOException {

        com.google.api.services.drive.Drive service = getGoogleClient(refreshToken, accessToken);
        Gmail gmailClient = getGmailClient(refreshToken, accessToken);
        AtomicInteger totalItemCount = new AtomicInteger();

        if (service == null || gmailClient == null) {
            throw new RuntimeException("Drive not found");
        }

        getTotalItemCount(service, gmailClient, totalItemCount);

        CustomDriveItem root = new CustomDriveItem();
        root.setName("root");
        root.setType("Folder");
        root.setChildren(performFetchAllGoogleDriveFiles(service, totalItemCount, simpMessagingTemplate, email));
        root.setEmails(performFetchAllGoogleEmails(gmailClient, totalItemCount, simpMessagingTemplate, email));

        return mapper.valueToTree(root);
    }

    private List<CustomDriveItem> performFetchAllGoogleDriveFiles(com.google.api.services.drive.Drive service,
                                                                  AtomicInteger totalItemCount,
                                                                  SimpMessagingTemplate simpMessagingTemplate,
                                                                  String email) throws IOException {
        List<CustomDriveItem> allFiles = new ArrayList<>();
        AtomicInteger itemsProcessed = new AtomicInteger();

        // Define the fields included in the response
        String fields = "nextPageToken, files(id, name, mimeType, createdTime, modifiedTime, webViewLink)";

        // Make the request, get the fields for each file and ignore folders
        com.google.api.services.drive.Drive.Files.List request = service.files().list().setFields(fields).setQ("mimeType != 'application/vnd.google-apps.folder' and 'me' in owners");

        FileList fileList;
        do {
            fileList = request.execute();
            for (File file : fileList.getFiles()) {

                // Convert the Google DateTime to Java OffsetDateTime
                OffsetDateTime googleCreatedDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(file.getCreatedTime().getValue()), ZoneId.systemDefault());
                OffsetDateTime googleModifiedDateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(file.getModifiedTime().getValue()), ZoneId.systemDefault());

                CustomDriveItem customItem = new CustomDriveItem();
                customItem.setId(file.getId());
                customItem.setName(file.getName());
                customItem.setType(file.getMimeType());
                customItem.setCreatedDateTime(googleCreatedDateTime);
                customItem.setLastModifiedDateTime(googleModifiedDateTime);
                customItem.setWebUrl(file.getWebViewLink());

                allFiles.add(customItem);
                itemsProcessed.getAndIncrement();
                simpMessagingTemplate.convertAndSendToUser(email, "/queue/progress", itemsProcessed.get() * 100 / totalItemCount.get());
            }
            request.setPageToken(fileList.getNextPageToken());
        } while (fileList.getNextPageToken() != null && fileList.getNextPageToken().length() > 0);

        return allFiles;
    }

    private List<CustomEmail> performFetchAllGoogleEmails(Gmail service,
                                                          AtomicInteger totalItemCount,
                                                          SimpMessagingTemplate simpMessagingTemplate,
                                                          String email) throws IOException {
        ListMessagesResponse response = service.users().messages().list("me").setQ("in:inbox AND is:read OR in:spam").execute();
        int emailPagesRetrieved = 0;
        AtomicInteger itemsProcessed = new AtomicInteger();

        List<CustomEmail> emails = new ArrayList<>();
        while (response.getMessages() != null) {
            response.getMessages().parallelStream().forEach(messageSummary -> {
                try {
                    Message message = service.users().messages().get("me", messageSummary.getId()).execute();
                    List<String> labels = message.getLabelIds();
                    if (labels.contains("INBOX") || labels.contains("SPAM")) {
                        CustomEmail customItem = new CustomEmail();
                        customItem.setId(message.getId());
                        customItem.setWebUrl("https://mail.google.com/mail/u/0/#inbox/" + message.getId());
                        customItem.setEmailSubject(message.getPayload().getHeaders().stream().filter(header -> header.getName().equals("Subject")).findFirst().get().getValue());
                        customItem.setReceivedDate(OffsetDateTime.ofInstant(Instant.ofEpochMilli(message.getInternalDate()), ZoneId.systemDefault()));
                        synchronized (emails) {
                            emails.add(customItem);
                            itemsProcessed.getAndIncrement();
                        }
                    }
                    simpMessagingTemplate.convertAndSendToUser(email, "/queue/progress", itemsProcessed.get() * 100 / totalItemCount.get());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            //Limit the amount of emails retrieved to 500 for performance reasons
            if (response.getNextPageToken() != null && emailPagesRetrieved < 4) {
                emailPagesRetrieved++;
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list("me").setQ("in:inbox OR in:spam is:read").setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        return emails;
    }

    private void getTotalItemCount(com.google.api.services.drive.Drive service, Gmail gmailClient, AtomicInteger totalItemCount) {
        try {
            int emailPagesRetrieved = 0;
            FileList fileList = service.files().list().setQ("mimeType != 'application/vnd.google-apps.folder' and 'me' in owners").execute();
            totalItemCount.getAndAdd(fileList.getFiles().size());
            ListMessagesResponse response = gmailClient.users().messages().list("me").setQ("in:inbox AND is:read OR in:spam").execute();
            while (response.getMessages() != null) {
                totalItemCount.getAndAdd(response.getMessages().size());
                if (response.getNextPageToken() != null && emailPagesRetrieved < 4) {
                    emailPagesRetrieved++;
                    String pageToken = response.getNextPageToken();
                    response = gmailClient.users().messages().list("me").setQ("in:inbox OR in:spam is:read").setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FilesDeletedResponse deleteRecommendedGoogleDriveFiles(JsonNode filesToDelete,
                                                                  String refreshToken,
                                                                  String accessToken,
                                                                  SimpMessagingTemplate simpMessagingTemplate,
                                                                  String email) throws JsonProcessingException {
        AtomicInteger filesDeleted = new AtomicInteger();
        AtomicInteger emailsDeleted = new AtomicInteger();
        AtomicInteger totalItemCount = new AtomicInteger();
        com.google.api.services.drive.Drive service = getGoogleClient(refreshToken, accessToken);
        Gmail gmailClient = getGmailClient(refreshToken, accessToken);
        CustomDriveItem filesInUserDrive = mapper.treeToValue(filesToDelete, CustomDriveItem.class);

        if (filesInUserDrive.getChildren() != null) {
            totalItemCount.getAndAdd(filesInUserDrive.getChildren().size());
        }

        if (filesInUserDrive.getEmails() != null) {
            totalItemCount.getAndAdd(filesInUserDrive.getEmails().size());
        }

        filesInUserDrive.getChildren().parallelStream().forEach(item -> {
            try {
                service.files().delete(item.getId()).execute();
                filesDeleted.getAndIncrement();
                simpMessagingTemplate.convertAndSendToUser(email, "/queue/deletion-progress", filesDeleted.get() * 100 / totalItemCount.get());

            } catch (Exception e) {
                throw new RuntimeException("Error deleting Google Drive files");
            }
        });

        if (filesInUserDrive.getEmails() != null) {
            filesInUserDrive.getEmails().parallelStream().forEach(item -> {
                try {
                    gmailClient.users().messages().delete("me", item.getId()).execute();
                    emailsDeleted.getAndIncrement();
                    simpMessagingTemplate.convertAndSendToUser(email, "/queue/deletion-progress", emailsDeleted.get() * 100 / totalItemCount.get());
                } catch (Exception e) {
                    throw new RuntimeException("Error deleting email item");
                }
            });
        }

        FilesDeletedResponse filesDeletedResponse = new FilesDeletedResponse();
        filesDeletedResponse.setFilesDeleted(filesDeleted.get());
        filesDeletedResponse.setEmailsDeleted(emailsDeleted.get());
        return filesDeletedResponse;
    }

    public JsonNode getDuplicatesFoundByAI(String provider, JsonNode files) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode allFiles = JsonUtils.extractChildren(files);

        // Convert allFiles to a list and sort it
        List<JsonNode> sortedFilesList = new ArrayList<>();
        allFiles.elements().forEachRemaining(sortedFilesList::add);
        sortedFilesList.sort(Comparator.comparing(node -> node.get("name").asText()));

        // Convert the sorted list back to a JsonNode
        JsonNode sortedFiles = mapper.valueToTree(sortedFilesList);

        int batchSize = 5;
        ObjectNode root = mapper.createObjectNode();
        root.set("id", (JsonNode) null);
        root.put("name", "root");
        root.put("type", "Folder");
        root.set("createdDateTime", (JsonNode) null);
        root.set("lastModifiedDateTime", (JsonNode) null);
        root.set("webUrl", (JsonNode) null);
        root.set("emails", (JsonNode) null);
        ArrayNode childrenArray = root.putArray("children");

        for (int i = 0; i < sortedFiles.size(); i += batchSize) {
            int end = Math.min(i + batchSize, sortedFiles.size());
            List<JsonNode> batch = new ArrayList<>(sortedFilesList.subList(i, end));
            ArrayNode batchNode = mapper.createArrayNode().addAll(batch);
            JsonNode batchRoot = mapper.createObjectNode();
            ((ObjectNode) batchRoot).putArray("children").addAll(batchNode);
            JsonNode batchResult = JsonUtils.removeEmailFields(batchRoot);
            String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(batchResult);
            JsonNode batchDuplicates = mapper.readTree(chatDiscussionWithAI(prettyJson, provider, 0));
            if (batchDuplicates.has("children")) {
                batchDuplicates.get("children").forEach(childrenArray::add);
            }
        }
        return root;
    }

    public static String chatDiscussionWithAI(String files, String provider, int timesTried) throws IOException {
        // OpenAI API endpoint
        String url = "https://api.openai.com/v1/chat/completions";
        String openApiKey = System.getenv("OPENAI_API_KEY");

        // Create HTTP client
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Create POST request
            HttpPost post = new HttpPost(url);

            // Set headers
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + openApiKey);

            // Create request body object
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");

            // Create messages array
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant analysing a JSON for duplicates by name key.");
            messages.add(systemMessage);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            //If statement is needed here due to how drives rename duplicate files
            if (provider.equals("GoogleDrive")) {
                userMessage.put("content", Constants.GOOGLE_CHAT_GPT_PROMPT + files);
            } else {
                userMessage.put("content", Constants.MICROSOFT_CHAT_GPT_PROMPT + files);
            }
            messages.add(userMessage);

            requestBody.put("messages", messages); // Add the messages parameter
            requestBody.put("max_tokens", 4096);

            // Convert request body object to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(requestBody);

            // Set request body
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);

            // Execute request and get response
            HttpResponse response = client.execute(post);

            //Sometimes we get a 400 error due to some issue with the AI's response so we will use recursion to try another 2 times
            if(response.getStatusLine().getStatusCode() != 200) {
                timesTried++;
                if(timesTried < 3) {
                    return chatDiscussionWithAI(files, provider, timesTried); // Return the result of the recursive call
                }
                return null;
            }

            // Extract response body
            HttpEntity responseEntity = response.getEntity();

            // Convert response body to JSON
            String responseBody = EntityUtils.toString(responseEntity);
            JsonNode responseJson = mapper.readTree(responseBody);

            // Extract 'content' field from the 'choices' array in the JSON response
            String content = responseJson.get("choices").get(0).get("message").get("content").asText();

            if (JsonUtils.validateContentFormat(content)) {
                return JsonUtils.transformJson(mapper.readTree(content)).toString();
            } else {
                throw new IOException("Invalid response format");
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public DriveInformationReponse mapToDriveInformationResponse(String displayName, String email, Double total, Double used) {

        DriveInformationReponse driveInformationReponse = new DriveInformationReponse();
        driveInformationReponse.setDisplayName(displayName);
        driveInformationReponse.setEmail(email);
        driveInformationReponse.setTotal(total);
        driveInformationReponse.setUsed(used);

        return driveInformationReponse;
    }

    private boolean compareDates(OffsetDateTime createdDateTime,
                                 OffsetDateTime lastModifiedDateTime,
                                 int recommendItemsCreatedDaysAgo,
                                 int recommendItemsNotChangedDaysAgo) {
        return createdDateTime.isBefore(OffsetDateTime.now().minusDays(recommendItemsCreatedDaysAgo)) ||
                lastModifiedDateTime.isBefore(OffsetDateTime.now().minusDays(recommendItemsNotChangedDaysAgo));
    }

    private boolean compareDatesOneDate(OffsetDateTime createdDateTime,
                                        int recommendItemsCreatedDaysAgo) {
        return createdDateTime.isBefore(OffsetDateTime.now().minusDays(recommendItemsCreatedDaysAgo));
    }
}
