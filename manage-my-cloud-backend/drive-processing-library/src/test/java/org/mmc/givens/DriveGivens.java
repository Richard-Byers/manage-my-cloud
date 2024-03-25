package org.mmc.givens;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.User;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.gson.JsonObject;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import org.mmc.pojo.CustomEmail;
import org.mmc.response.CustomDriveItem;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveGivens {

    private static final ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    public static Drive generateDrive() {
        Drive drive = new Drive();
        drive.id = "testDriveId";
        drive.quota = new Quota();
        drive.quota.total = 100L;
        drive.quota.used = 50L;

        drive.owner = new IdentitySet();
        drive.owner.user = new Identity();

        return drive;
    }

    public static Drive generateDriveWithEmail() {
        JsonObject email = new JsonObject();
        email.addProperty("email", "johndoe@gmail.com");

        Drive drive = new Drive();
        drive.id = "testDriveId";
        drive.quota = new Quota();
        drive.quota.total = 100L;
        drive.quota.used = 50L;

        drive.owner = new IdentitySet();
        drive.owner.user = new Identity();

        drive.owner.user.additionalDataManager().put("email", email.get("email"));

        return drive;
    }

    public static DriveItemCollectionPage generateDriveItemCollectionPage() {
        List<DriveItem> driveItems = new ArrayList<>();

        DriveItem subDriveItem = new DriveItem();
        subDriveItem.id = "testSubDriveItemId";
        subDriveItem.name = "testSubFile";
        subDriveItem.file = new File();
        subDriveItem.file.mimeType = "text/plain";
        subDriveItem.createdDateTime = OffsetDateTime.now();
        subDriveItem.lastModifiedDateTime = OffsetDateTime.now();
        subDriveItem.webUrl = "https://testSubFile.com";

        driveItems.add(subDriveItem);

        return new DriveItemCollectionPage(driveItems, null);
    }

    public static DriveItemCollectionPage generateDriveItemCollectionPageFolder() {
        List<DriveItem> driveItems = new ArrayList<>();

        DriveItem driveItem = new DriveItem();
        driveItem.id = "testDriveItemId";
        driveItem.name = "testFolder";
        driveItem.folder = new Folder();
        driveItem.createdDateTime = OffsetDateTime.now();
        driveItem.lastModifiedDateTime = OffsetDateTime.now();
        driveItem.webUrl = "https://testFolder.com";

        driveItems.add(driveItem);

        return new DriveItemCollectionPage(driveItems, null);
    }

    public static About generateGoogleDriveAbout() {
        About about = new About();
        about.setStorageQuota(new About.StorageQuota().setLimit(100L).setUsage(50L));
        about.setUser(new User().setDisplayName("John Doe"));
        return about;
    }

    public static JsonNode generateItemsToDelete() {
        CustomDriveItem recommendations = new CustomDriveItem();
        recommendations.setName("root");
        recommendations.setType("Folder");

        ArrayList<CustomDriveItem> children = new ArrayList<>();
        ArrayList<CustomEmail> emails = new ArrayList<>();
        children.add(new CustomDriveItem("1", "name1.png", "application/png", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));
        children.add(new CustomDriveItem("2", "name1.mp4", "mp4", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));
        children.add(new CustomDriveItem("3", "name1.csv", "application/png", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));

        emails.add(new CustomEmail("id", "url", OffsetDateTime.now(), "subject"));

        recommendations.setChildren(Collections.synchronizedList(children));
        recommendations.setEmails(Collections.synchronizedList(emails));

        return objectMapper.valueToTree(recommendations);
    }

    public static JsonNode generateItemsToDeleteWithUnsupportedFileTypes() {
        CustomDriveItem recommendations = new CustomDriveItem();
        recommendations.setName("root");
        recommendations.setType("Folder");

        ArrayList<CustomDriveItem> children = new ArrayList<>();
        children.add(new CustomDriveItem("1", "name1.png", "application/png", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));
        children.add(new CustomDriveItem("2", "name1.mp4", "mp4", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));
        children.add(new CustomDriveItem("3", "name1.csv", "application/png", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));
        children.add(new CustomDriveItem("4", "name1.log", "log", OffsetDateTime.now(), OffsetDateTime.now(), "url", List.of(new CustomDriveItem()), List.of(new CustomEmail()), true));
        recommendations.setChildren(Collections.synchronizedList(children));


        return objectMapper.valueToTree(recommendations);
    }

    public static FileList generateGoogleDriveFiles() {
        FileList fileList = new FileList();
        com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
        file.setId("testFileId");
        file.setName("testFile");
        file.setMimeType("text/plain");
        file.setCreatedTime(new com.google.api.client.util.DateTime(System.currentTimeMillis()));
        file.setModifiedTime(new com.google.api.client.util.DateTime(System.currentTimeMillis()));
        file.setWebViewLink("https://testFile.com");
        fileList.setFiles(Collections.singletonList(file));
        fileList.setNextPageToken(null);

        return fileList;
    }

    public static ListMessagesResponse generateGmailMessages() {
        ListMessagesResponse listMessagesResponse = new ListMessagesResponse();
        Message message = new Message();
        message.setId("testMessageId");
        message.setInternalDate(System.currentTimeMillis());
        message.setPayload(new com.google.api.services.gmail.model.MessagePart()
                .setBody(new com.google.api.services.gmail.model.MessagePartBody().setData("testData"))
                .setHeaders(Collections.singletonList(new com.google.api.services.gmail.model.MessagePartHeader().setName("Subject").setValue("testValue"))));
        listMessagesResponse.setMessages(Collections.singletonList(message));
        listMessagesResponse.setNextPageToken(null);

        return listMessagesResponse;
    }

    public static Message generateGmailMessage() {
        ListMessagesResponse listMessagesResponse = new ListMessagesResponse();
        Message message = new Message();
        message.setId("testMessageId");
        message.setLabelIds(Collections.singletonList("INBOX"));
        message.setInternalDate(System.currentTimeMillis());
        message.setPayload(new com.google.api.services.gmail.model.MessagePart()
                .setBody(new com.google.api.services.gmail.model.MessagePartBody().setData("testData"))
                .setHeaders(Collections.singletonList(new com.google.api.services.gmail.model.MessagePartHeader().setName("Subject").setValue("testValue"))));
        listMessagesResponse.setMessages(Collections.singletonList(new com.google.api.services.gmail.model.Message()));
        listMessagesResponse.setNextPageToken(null);

        return message;
    }

}
