package org.mmc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.model.About;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.requests.*;
import okhttp3.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mmc.auth.DriveAuthManager;
import org.mmc.drive.DriveInformationService;
import org.mmc.pojo.UserPreferences;
import org.mmc.response.DriveInformationReponse;
import org.mmc.response.FilesDeletedResponse;
import org.mmc.util.JsonUtils;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);

    private final String EMAIL = "johndoe@gmail.com";

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private HttpEntity httpEntity;

    @Mock
    private StatusLine statusLine;

    @BeforeEach
    public void setUp() {
        Mockito.doNothing().when(simpMessagingTemplate).convertAndSend(any(), any(), any(), any());
    }

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

            JsonNode driveInformationReponse = driveInformationService.listAllItemsInOneDrive(accessToken, expiryDate, simpMessagingTemplate, EMAIL);

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

        AtomicInteger callCount = new AtomicInteger(0);

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
                int count = callCount.getAndIncrement();
                if (count == 1) {
                    return mockDrive;
                } else {
                    return mockSubDrive;
                }
            });

            JsonNode driveInformationReponse = driveInformationService.listAllItemsInOneDrive(accessToken, expiryDate, simpMessagingTemplate, EMAIL);

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
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences, simpMessagingTemplate, EMAIL);
        //then
        assertEquals(3, recommendedItems.get("children").size());
    }

    @Test
    public void returnItemsToDelete_onlyImages_returnAllImageItems() throws Exception {
        //given
        UserPreferences userPreferences = generateUserPreferencesOnlyImage0Days();
        JsonNode itemsInDrive = generateItemsToDelete();

        //when
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences, simpMessagingTemplate, EMAIL);
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
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences, simpMessagingTemplate, EMAIL);
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
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences, simpMessagingTemplate, EMAIL);
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
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences, simpMessagingTemplate, EMAIL);
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
        JsonNode recommendedItems = driveInformationService.returnItemsToDelete(itemsInDrive, userPreferences, simpMessagingTemplate, EMAIL);
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

            FilesDeletedResponse filesDeletedResponse = driveInformationService.deleteRecommendedOneDriveFiles(itemsToDelete, accessToken, expiryDate, simpMessagingTemplate, EMAIL);

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

            Exception exception = assertThrows(RuntimeException.class, () -> driveInformationService.deleteRecommendedOneDriveFiles(itemsToDelete, accessToken, expiryDate, simpMessagingTemplate, EMAIL));
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

            JsonNode driveFiles = driveInformationService.fetchAllGoogleDriveFiles(refreshToken, accessToken, simpMessagingTemplate, EMAIL);

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

            FilesDeletedResponse filesDeletedResponse = driveInformationService.deleteRecommendedGoogleDriveFiles(itemsToDelete, refreshToken, accessToken, simpMessagingTemplate, EMAIL);

            //then
            assertEquals(3, filesDeletedResponse.getFilesDeleted());
            assertEquals(1, filesDeletedResponse.getEmailsDeleted());
        }
    }

    @Test
    public void testGetDuplicatesFoundByAI() throws IOException, InterruptedException {
        // Mock the chatDiscussionWithAI method
        try (MockedStatic<DriveInformationService> mockedDriveInformationService = Mockito.mockStatic(DriveInformationService.class)) {
            mockedDriveInformationService.when(() -> DriveInformationService.chatDiscussionWithAI(any(), any(), eq(0)))
                    .thenReturn("{\"duplicates\":[{\"name\":\"file.txt\",\"count\":2,\"files\":[{\"id\":\"123\",\"name\":\"file.txt\",\"type\":\"file\",\"createdDateTime\":\"2023-03-19T10:00:00Z\",\"lastModifiedDateTime\":\"2023-03-19T10:00:00Z\",\"webUrl\":\"https://example.com/file.txt\"},{\"id\":\"456\",\"name\":\"file.txt\",\"type\":\"file\",\"createdDateTime\":\"2023-03-19T11:00:00Z\",\"lastModifiedDateTime\":\"2023-03-19T11:00:00Z\",\"webUrl\":\"https://example.com/file.txt\"}]}]}");

            // Mock the JsonUtils.removeEmailFields method
            JsonNode files = new ObjectMapper().readTree("{\"name\":\"root\",\"children\":[{\"id\":\"123\",\"name\":\"file.txt\",\"type\":\"file\"}]}");
            JsonNode expectedResult = new ObjectMapper().readTree("{\"duplicates\":[{\"name\":\"file.txt\",\"count\":2,\"files\":[{\"id\":\"123\",\"name\":\"file.txt\",\"type\":\"file\",\"createdDateTime\":\"2023-03-19T10:00:00Z\",\"lastModifiedDateTime\":\"2023-03-19T10:00:00Z\",\"webUrl\":\"https://example.com/file.txt\"},{\"id\":\"456\",\"name\":\"file.txt\",\"type\":\"file\",\"createdDateTime\":\"2023-03-19T11:00:00Z\",\"lastModifiedDateTime\":\"2023-03-19T11:00:00Z\",\"webUrl\":\"https://example.com/file.txt\"}]}]}");

            JsonNode result = new DriveInformationService().getDuplicatesFoundByAI("GoogleDrive", files);

            assertEquals(expectedResult, result);
        }
    }

    @Test
    public void testChatDiscussionWithAI() throws IOException {
        // Initialize mocks
        httpClient = Mockito.mock(CloseableHttpClient.class);
        httpResponse = Mockito.mock(CloseableHttpResponse.class);
        httpEntity = Mockito.mock(HttpEntity.class);
        statusLine = Mockito.mock(StatusLine.class);

        try (MockedStatic<HttpClients> httpClientsMock = Mockito.mockStatic(HttpClients.class)) {
            httpClientsMock.when(HttpClients::createDefault).thenReturn(httpClient);

            // Mock the API response
            String responseBody = "{\"choices\":[{\"message\":{\"content\":\"{\\\"duplicates\\\":[{\\\"name\\\":\\\"file.txt\\\",\\\"count\\\":2,\\\"files\\\":[{\\\"id\\\":\\\"123\\\",\\\"name\\\":\\\"file.txt\\\",\\\"type\\\":\\\"file\\\",\\\"createdDateTime\\\":\\\"2023-03-19T10:00:00Z\\\",\\\"lastModifiedDateTime\\\":\\\"2023-03-19T10:00:00Z\\\",\\\"webUrl\\\":\\\"https://example.com/file.txt\\\"},{\\\"id\\\":\\\"456\\\",\\\"name\\\":\\\"file.txt\\\",\\\"type\\\":\\\"file\\\",\\\"createdDateTime\\\":\\\"2023-03-19T11:00:00Z\\\",\\\"lastModifiedDateTime\\\":\\\"2023-03-19T11:00:00Z\\\",\\\"webUrl\\\":\\\"https://example.com/file.txt\\\"}]}]}\"}}]}";

            // Mock the HTTP client behavior
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8)));
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

            // Prepare the files parameter
            String files = "{\n" +
                    "  \"id\" : null,\n" +
                    "  \"name\" : \"root\",\n" +
                    "  \"type\" : \"Folder\",\n" +
                    "  \"createdDateTime\" : null,\n" +
                    "  \"lastModifiedDateTime\" : null,\n" +
                    "  \"webUrl\" : null,\n" +
                    "  \"children\" : [ {\n" +
                    "    \"id\" : \"1kDnvf1fZ6F_fgVByNKosPwECDy8XegCb\",\n" +
                    "    \"name\" : \"SaulGone (2) copy 2 (4).jpg\",\n" +
                    "    \"type\" : \"image/jpeg\",\n" +
                    "    \"createdDateTime\" : 1.710871335593E9,\n" +
                    "    \"lastModifiedDateTime\" : 1709945523,\n" +
                    "    \"webUrl\" : \"https://drive.google.com/file/d/1kDnvf1fZ6F_fgVByNKosPwECDy8XegCb/view?usp=drivesdk\",\n" +
                    "    \"children\" : null\n" +
                    "  }, {\n" +
                    "    \"id\" : \"1rq8gDtQnqMfv3TFLq4PVmNA15KrlMcKk\",\n" +
                    "    \"name\" : \"SaulGone (2) copy 2 (3).jpg\",\n" +
                    "    \"type\" : \"image/jpeg\",\n" +
                    "    \"createdDateTime\" : 1.710871317885E9,\n" +
                    "    \"lastModifiedDateTime\" : 1709945523,\n" +
                    "    \"webUrl\" : \"https://drive.google.com/file/d/1rq8gDtQnqMfv3TFLq4PVmNA15KrlMcKk/view?usp=drivesdk\",\n" +
                    "    \"children\" : null\n" +
                    "  }]\n" +
                    "}";

            // Mock the JsonUtils.validateContentFormat method
            try (MockedStatic<JsonUtils> mockedJsonUtils = Mockito.mockStatic(JsonUtils.class)) {
                mockedJsonUtils.when(() -> JsonUtils.validateContentFormat(any())).thenReturn(true);
                mockedJsonUtils.when(() -> JsonUtils.transformJson(any())).thenCallRealMethod();

                String result = DriveInformationService.chatDiscussionWithAI(files, "GoogleDrive", 0);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode expectedResult = objectMapper.readTree("{\"id\":null,\"name\":\"root\",\"type\":\"Folder\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":null,\"children\":[{\"id\":\"123\",\"name\":\"file.txt\",\"type\":\"file\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://example.com/file.txt\",\"children\":[],\"emails\":null},{\"id\":\"456\",\"name\":\"file.txt\",\"type\":\"file\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://example.com/file.txt\",\"children\":[],\"emails\":null}],\"emails\":null}");
                String expectedResultAsString = objectMapper.writeValueAsString(expectedResult);

                assertEquals(expectedResultAsString, result);
            }
        }
    }

    @Test
    public void testChatDiscussionWithAI_InvalidResponseFormat() throws IOException {
        // Initialize mocks
        httpClient = Mockito.mock(CloseableHttpClient.class);
        httpResponse = Mockito.mock(CloseableHttpResponse.class);
        httpEntity = Mockito.mock(HttpEntity.class);
        statusLine = Mockito.mock(StatusLine.class);

        try (MockedStatic<HttpClients> httpClientsMock = Mockito.mockStatic(HttpClients.class)) {
            httpClientsMock.when(HttpClients::createDefault).thenReturn(httpClient);

            // Mock an invalid API response
            String responseBody = "{\"choices\":[{\"message\":{\"content\":\"invalid response\"}}]}";

            // Mock the HTTP client behavior
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8)));
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

            // Prepare the files parameter
            String files = "{\n" +
                    "  \"id\" : null,\n" +
                    "  \"name\" : \"root\",\n" +
                    "  \"type\" : \"Folder\",\n" +
                    "  \"createdDateTime\" : null,\n" +
                    "  \"lastModifiedDateTime\" : null,\n" +
                    "  \"webUrl\" : null,\n" +
                    "  \"children\" : [ {\n" +
                    "    \"id\" : \"1kDnvf1fZ6F_fgVByNKosPwECDy8XegCb\",\n" +
                    "    \"name\" : \"SaulGone (2) copy 2 (4).jpg\",\n" +
                    "    \"type\" : \"image/jpeg\",\n" +
                    "    \"createdDateTime\" : 1.710871335593E9,\n" +
                    "    \"lastModifiedDateTime\" : 1709945523,\n" +
                    "    \"webUrl\" : \"https://drive.google.com/file/d/1kDnvf1fZ6F_fgVByNKosPwECDy8XegCb/view?usp=drivesdk\",\n" +
                    "    \"children\" : null\n" +
                    "  }, {\n" +
                    "    \"id\" : \"1rq8gDtQnqMfv3TFLq4PVmNA15KrlMcKk\",\n" +
                    "    \"name\" : \"SaulGone (2) copy 2 (3).jpg\",\n" +
                    "    \"type\" : \"image/jpeg\",\n" +
                    "    \"createdDateTime\" : 1.710871317885E9,\n" +
                    "    \"lastModifiedDateTime\" : 1709945523,\n" +
                    "    \"webUrl\" : \"https://drive.google.com/file/d/1rq8gDtQnqMfv3TFLq4PVmNA15KrlMcKk/view?usp=drivesdk\",\n" +
                    "    \"children\" : null\n" +
                    "  }]\n" +
                    "}";

            // Mock the JsonUtils.validateContentFormat method to return false
            try (MockedStatic<JsonUtils> mockedJsonUtils = Mockito.mockStatic(JsonUtils.class)) {
                mockedJsonUtils.when(() -> JsonUtils.validateContentFormat(any())).thenReturn(false);

                assertThrows(IOException.class, () -> DriveInformationService.chatDiscussionWithAI(files, "GoogleDrive", 0));
            }
        }
    }
}
