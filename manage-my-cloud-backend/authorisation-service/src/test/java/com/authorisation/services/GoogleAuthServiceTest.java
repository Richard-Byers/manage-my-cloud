//import com.authorisation.services.GoogleAuthService;
//import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class GoogleAuthServiceTest {
//
//    @Mock
//    private GoogleAuthService googleAuthService;
//
//    @InjectMocks
//    private GoogleAuthService googleAuthServiceUnderTest;
//
//    private GoogleTokenResponse tokenResponse;
//
//    @BeforeEach
//    public void setup() {
//        // Create a GoogleTokenResponse object and set the necessary fields
//        tokenResponse = new GoogleTokenResponse();
//        tokenResponse.setAccessToken("access_token");
//        tokenResponse.setIdToken("id_token");
//        tokenResponse.setRefreshToken("refresh_token");
//    }
//
//    @Test
//    public void storeAuthCodeTest() throws Exception {
//        // Mock the getGoogleTokenResponse method in GoogleAuthService to return the GoogleTokenResponse object
//        when(googleAuthService.getGoogleTokenResponse(anyString())).thenReturn(tokenResponse);
//
//        // Call storeAuthCode and check the results
//        googleAuthServiceUnderTest.storeAuthCode("{\"authCode\":\"4/0AfJohXnSbVBJQR7PG35P1gHkn5KYEWALPjQ5U2zx_9wXLDxODxq6tAnyyKRBkZi4xZ9NBQ\"}");
//    }
//}