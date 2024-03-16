package org.mmc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.drive.model.About;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.requests.*;
import okhttp3.Request;
import org.junit.Test;
import org.mmc.auth.DriveAuthManager;
import org.mmc.drive.DriveInformationService;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmc.givens.DriveGivens.*;
import static org.mmc.givens.DriveInformationResponseGivens.*;
import static org.mmc.givens.UserPreferencesGivens.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DriveInformationServiceTest {

    private final DriveInformationService driveInformationService = new DriveInformationService();

    @Test
    public void getOneDriveInformation_ReturnsDriveInformation() {
        //given
        Drive mockDrive = generateDrive();
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveRequest driveRequest = mock(DriveRequest.class);
        DriveInformationReponse expectedDriveInformationReponse = generateDriveInformationResponseNoEmail(mockDrive);

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);
        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.buildRequest()).thenReturn(driveRequest);
            when(driveRequest.get()).thenReturn(mockDrive);

            DriveInformationReponse driveInformationReponse = driveInformationService.getOneDriveInformation(accessToken, expiryDate);

            //then
            assertEquals(expectedDriveInformationReponse.getEmail(), driveInformationReponse.getEmail());
            assertEquals(expectedDriveInformationReponse.getDisplayName(), driveInformationReponse.getDisplayName());
            assertEquals(expectedDriveInformationReponse.getTotal(), driveInformationReponse.getTotal());
            assertEquals(expectedDriveInformationReponse.getUsed(), driveInformationReponse.getUsed());
        }
    }

    @Test
    public void getOneDriveInformation_linkedUserEmail_ReturnsDriveInformation() {
        //given
        Drive mockDrive = generateDriveWithEmail();
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveRequest driveRequest = mock(DriveRequest.class);
        DriveInformationReponse expectedDriveInformationReponse = generateDriveInformationResponseWithEmail(mockDrive);

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);
        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.buildRequest()).thenReturn(driveRequest);
            when(driveRequest.get()).thenReturn(mockDrive);

            DriveInformationReponse driveInformationReponse = driveInformationService.getOneDriveInformation(accessToken, expiryDate);

            //then
            assertEquals(expectedDriveInformationReponse.getEmail(), driveInformationReponse.getEmail());
            assertEquals(expectedDriveInformationReponse.getDisplayName(), driveInformationReponse.getDisplayName());
            assertEquals(expectedDriveInformationReponse.getTotal(), driveInformationReponse.getTotal());
            assertEquals(expectedDriveInformationReponse.getUsed(), driveInformationReponse.getUsed());
        }
    }

    @Test
    public void getOneDriveInformation_nullDrive_throwsException() {
        //given
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveRequest driveRequest = mock(DriveRequest.class);

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);
        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.buildRequest()).thenReturn(driveRequest);
            when(driveRequest.get()).thenReturn(null);

            RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> driveInformationService.getOneDriveInformation(accessToken, expiryDate));
            //then
            assertEquals("Drive not found", runtimeException.getMessage());
        }
    }

    @Test
    public void getGoogleDriveInformation_ReturnsDriveInformation() throws IOException {
        //given
        com.google.api.services.drive.Drive mockDrive = mock(com.google.api.services.drive.Drive.class);
        com.google.api.services.drive.Drive.About about = mock(com.google.api.services.drive.Drive.About.class);
        com.google.api.services.drive.Drive.About.Get get = mock(com.google.api.services.drive.Drive.About.Get.class);
        About aboutResponse = generateGoogleDriveAbout();
        String email = "johndoe@gmail.com";
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        DriveInformationReponse expectedDriveInformationReponse = generateGoogleDriveInformationResponseWithEmail(aboutResponse, email);

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getGoogleClient(anyString(), anyString())).thenReturn(mockDrive);
            when(mockDrive.about()).thenReturn(about);
            when(about.get()).thenReturn(get);
            when(get.setFields(anyString())).thenReturn(get);
            when(get.execute()).thenReturn(aboutResponse);

            DriveInformationReponse driveInformationReponse = driveInformationService.getGoogleDriveInformation(email, refreshToken, accessToken);

            //then
            assertEquals(expectedDriveInformationReponse.getEmail(), driveInformationReponse.getEmail());
            assertEquals(expectedDriveInformationReponse.getDisplayName(), driveInformationReponse.getDisplayName());
            assertEquals(expectedDriveInformationReponse.getTotal(), driveInformationReponse.getTotal());
            assertEquals(expectedDriveInformationReponse.getUsed(), driveInformationReponse.getUsed());
        }
    }

    @Test
    public void listAllItemsInOneDrive_returnsItemsInDrive() throws Exception {
        //given
        DriveItemCollectionPage mockDrive = generateDriveItemCollectionPage();
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveItemRequestBuilder driveItemRequestBuilder = mock(DriveItemRequestBuilder.class);
        DriveItemCollectionRequestBuilder driveItemCollectionRequestBuilder = mock(DriveItemCollectionRequestBuilder.class);
        DriveItemCollectionRequest mockRequest = mock(DriveItemCollectionRequest.class);

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.root()).thenReturn(driveItemRequestBuilder);
            when(driveItemRequestBuilder.children()).thenReturn(driveItemCollectionRequestBuilder);
            when(driveItemCollectionRequestBuilder.buildRequest()).thenReturn(mockRequest);
            when(driveRequestBuilder.items(anyString())).thenReturn(driveItemRequestBuilder);
            when(mockRequest.get()).thenReturn(mockDrive);

            JsonNode driveInformationReponse = driveInformationService.listAllItemsInOneDrive(accessToken, expiryDate);

            //then
            assertEquals(1, driveInformationReponse.get("children").size());
            assertEquals("testSubDriveItemId", driveInformationReponse.get("children").get(0).get("id").asText());
        }
    }

    @Test
    public void listAllSubItemsInOneDrive_returnsItemsInDrive() throws Exception {
        //given
        DriveItemCollectionPage mockDrive = generateDriveItemCollectionPageFolder();
        DriveItemCollectionPage mockSubDrive = generateDriveItemCollectionPage();
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveItemRequestBuilder driveItemRequestBuilder = mock(DriveItemRequestBuilder.class);
        DriveItemCollectionRequestBuilder driveItemCollectionRequestBuilder = mock(DriveItemCollectionRequestBuilder.class);
        DriveItemCollectionRequest mockRequest = mock(DriveItemCollectionRequest.class);

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);

        AtomicBoolean firstCall = new AtomicBoolean(true);

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(driveRequestBuilder.root()).thenReturn(driveItemRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.items(anyString())).thenReturn(driveItemRequestBuilder);
            when(driveItemRequestBuilder.children()).thenReturn(driveItemCollectionRequestBuilder);
            when(driveItemCollectionRequestBuilder.buildRequest()).thenReturn(mockRequest);
            when(mockRequest.get()).thenAnswer(invocation -> {
                if (firstCall.get()) {
                    firstCall.set(false);
                    return mockDrive;
                } else {
                    return mockSubDrive;
                }
            });

            JsonNode driveInformationReponse = driveInformationService.listAllItemsInOneDrive(accessToken, expiryDate);

            //then
            assertEquals(2, driveInformationReponse.get("children").size());
            assertEquals("testSubDriveItemId", driveInformationReponse.get("children").get(0).get("id").asText());
        }
    }

    @Test
    public void returnItemsToDelete_allPreferencesTrue_returnAllItems() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesAllTrue0Days();
        JsonNode itemsInDrive = generateItemsToDelete();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences);
        //then
        assertEquals(3, recommendedItems.get("children").size());
    }

    @Test
    public void returnItemsToDelete_onlyImages_returnAllImageItems() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesOnlyImage0Days();
        JsonNode itemsInDrive = generateItemsToDelete();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences);
        //then
        assertEquals(1, recommendedItems.get("children").size());
        assertEquals("name1.png", recommendedItems.get("children").get(0).get("name").asText());
    }

    @Test
    public void returnItemsToDelete_onlyDocuments_returnAllDocumentItems() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesOnlyDocument0Days();
        JsonNode itemsInDrive = generateItemsToDelete();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences);
        //then
        assertEquals(1, recommendedItems.get("children").size());
        assertEquals("name1.csv", recommendedItems.get("children").get(0).get("name").asText());
    }

    @Test
    public void returnItemsToDelete_onlyVideos_returnAllVideoItems() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesOnlyVideo0Days();
        JsonNode itemsInDrive = generateItemsToDelete();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences);
        //then
        assertEquals(1, recommendedItems.get("children").size());
        assertEquals("name1.mp4", recommendedItems.get("children").get(0).get("name").asText());
    }

    @Test
    public void returnItemsToDelete_onlyOther_returnAllOtherFileTypes() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesOnlyUnknownFileTypes0Days();
        JsonNode itemsInDrive = generateItemsToDeleteWithUnsupportedFileTypes();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences);
        //then
        assertEquals(1, recommendedItems.get("children").size());
        assertEquals("name1.log", recommendedItems.get("children").get(0).get("name").asText());
    }

    @Test
    public void returnItemsToDelete_daysCreatedLessThanPreferences_returnNoFiles() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesOnlyUnknownFileTypes7Days();
        JsonNode itemsInDrive = generateItemsToDeleteWithUnsupportedFileTypes();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences);
        //then
        assertEquals(0, recommendedItems.get("children").size());
    }

    @Test
    public void deleteRecommendedOneDriveFiles_returnsFilesDeletedResponse() throws Exception {
        //given
        JsonNode itemsToDelete = generateItemsToDelete();
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveItemRequestBuilder driveItemRequestBuilder = mock(DriveItemRequestBuilder.class);
        DriveItemRequest driveItemRequest = mock(DriveItemRequest.class);

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.items(anyString())).thenReturn(driveItemRequestBuilder);
            when(driveItemRequestBuilder.buildRequest()).thenReturn(driveItemRequest);
            when(driveItemRequest.delete()).thenReturn(null);

            FilesDeletedResponse filesDeletedResponse = driveInformationService.deleteRecommendedOneDriveFiles(itemsToDelete, accessToken, expiryDate);

            //then
            assertEquals(3, filesDeletedResponse.getFilesDeleted());
        }
    }

    @Test
    public void deleteRecommendedOneDriveFiles_errorDeletingFile_throwsException() {
        //given
        JsonNode itemsToDelete = generateItemsToDelete();
        UserRequestBuilder userRequestBuilder = mock(UserRequestBuilder.class);
        DriveRequestBuilder driveRequestBuilder = mock(DriveRequestBuilder.class);
        DriveItemRequestBuilder driveItemRequestBuilder = mock(DriveItemRequestBuilder.class);
        DriveItemRequest driveItemRequest = mock(DriveItemRequest.class);
        String expectedExceptionMessage = "Error deleting OneDrive files";

        Date expiryDate = new Date();
        String accessToken = "testAccessToken";
        GraphServiceClient<Request> mockGraphClient = mock(GraphServiceClient.class);

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getOneDriveClient(anyString(), any(Date.class))).thenReturn(mockGraphClient);
            when(mockGraphClient.me()).thenReturn(userRequestBuilder);
            when(userRequestBuilder.drive()).thenReturn(driveRequestBuilder);
            when(driveRequestBuilder.items(anyString())).thenReturn(driveItemRequestBuilder);
            when(driveItemRequestBuilder.buildRequest()).thenThrow(new RuntimeException(expectedExceptionMessage));
            when(driveItemRequest.delete()).thenReturn(null);

            Exception exception = assertThrows(RuntimeException.class, () -> driveInformationService.deleteRecommendedOneDriveFiles(itemsToDelete, accessToken, expiryDate));
            String actualExceptionMessage = exception.getMessage();
            //then
            assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
        }
    }

    @Test
    public void mapToDriveInformationResponse_ReturnsDriveInformationResponse() {
        String displayName = "Test User";
        String email = "johnDoe@gmail.com";
        Double total = 100.0;
        Double used = 50.0;

        DriveInformationReponse driveInformationReponse = driveInformationService.mapToDriveInformationResponse(displayName, email, total, used);
        assertEquals(displayName, driveInformationReponse.getDisplayName());
        assertEquals(email, driveInformationReponse.getEmail());
        assertEquals(total, driveInformationReponse.getTotal());
        assertEquals(used, driveInformationReponse.getUsed());
    }

    @Test
    public void fetchAllGoogleDriveFiles_returnsDriveFiles() throws IOException {
        //given
        com.google.api.services.drive.Drive mockDrive = mock(com.google.api.services.drive.Drive.class);
        Gmail mockGmail = mock(Gmail.class);
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        com.google.api.services.drive.Drive.Files files = mock(com.google.api.services.drive.Drive.Files.class);
        com.google.api.services.drive.Drive.Files.List list = mock(com.google.api.services.drive.Drive.Files.List.class);
        Gmail.Users users = mock(Gmail.Users.class);
        Gmail.Users.Messages messages = mock(Gmail.Users.Messages.class);
        Gmail.Users.Messages.List listMessages = mock(Gmail.Users.Messages.List.class);
        Gmail.Users.Messages.Get getMessages = mock(Gmail.Users.Messages.Get.class);
        Message message = generateGmailMessage();

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getGoogleClient(anyString(), anyString())).thenReturn(mockDrive);
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getGmailClient(anyString(), anyString())).thenReturn(mockGmail);
            when(mockDrive.files()).thenReturn(files);
            when(files.list()).thenReturn(list);
            when(list.setFields(anyString())).thenReturn(list);
            when(list.setQ(anyString())).thenReturn(list);
            when(list.execute()).thenReturn(generateGoogleDriveFiles());

            when(mockGmail.users()).thenReturn(users);
            when(users.messages()).thenReturn(messages);
            when(messages.list(anyString())).thenReturn(listMessages);
            when(listMessages.setQ(anyString())).thenReturn(listMessages);
            when(listMessages.execute()).thenReturn(generateGmailMessages());

            when(mockGmail.users()).thenReturn(users);
            when(users.messages()).thenReturn(messages);
            when(messages.get(anyString(), anyString())).thenReturn(getMessages);
            when(getMessages.execute()).thenReturn(message);

            JsonNode driveFiles = driveInformationService.fetchAllGoogleDriveFiles(refreshToken, accessToken, true);

            //then
            assertEquals(1, driveFiles.get("children").size());
            assertEquals("testFileId", driveFiles.get("children").get(0).get("id").asText());
            assertEquals(1, driveFiles.get("emails").size());
            assertEquals("testMessageId", driveFiles.get("emails").get(0).get("id").asText());
        }
    }

    @Test
    public void deleteRecommendedGoogleDriveFiles_returnsFilesDeletedResponse() throws IOException {
        //given
        com.google.api.services.drive.Drive mockDrive = mock(com.google.api.services.drive.Drive.class);
        Gmail mockGmail = mock(Gmail.class);
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        JsonNode itemsToDelete = generateItemsToDelete();
        com.google.api.services.drive.Drive.Files files = mock(com.google.api.services.drive.Drive.Files.class);
        Gmail.Users users = mock(Gmail.Users.class);
        Gmail.Users.Messages messages = mock(Gmail.Users.Messages.class);
        com.google.api.services.drive.Drive.Files.Delete delete = mock(com.google.api.services.drive.Drive.Files.Delete.class);
        Gmail.Users.Messages.Delete deleteMessage = mock(Gmail.Users.Messages.Delete.class);

        try (MockedStatic<DriveAuthManager> driveAuthManagerMockedStatic = Mockito.mockStatic(DriveAuthManager.class)) {
            //when
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getGoogleClient(anyString(), anyString())).thenReturn(mockDrive);
            driveAuthManagerMockedStatic.when(() -> DriveAuthManager.getGmailClient(anyString(), anyString())).thenReturn(mockGmail);

            when(mockDrive.files()).thenReturn(files);
            when(files.delete(anyString())).thenReturn(delete);

            when(mockGmail.users()).thenReturn(users);
            when(users.messages()).thenReturn(messages);
            when(messages.delete(anyString(), anyString())).thenReturn(deleteMessage);

            FilesDeletedResponse filesDeletedResponse = driveInformationService.deleteRecommendedGoogleDriveFiles(itemsToDelete, refreshToken, accessToken);

            //then
            assertEquals(3, filesDeletedResponse.getFilesDeleted());
            assertEquals(1, filesDeletedResponse.getEmailsDeleted());
        }
    }
}
