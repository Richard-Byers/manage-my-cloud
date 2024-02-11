package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;
import com.authorisation.repositories.CloudPlatformRepository;
import com.authorisation.repositories.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.authorisation.givens.CloudPlatformGivens.generateCloudPlatform;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudPlatformServiceTest {

    @Mock
    private CloudPlatformRepository cloudPlatformRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private CloudPlatformService cloudPlatformService;

    @Test
    void addCloudPlatform() {
        String userEmail = "email@example.com";
        String platformName = "OneDrive";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        CloudPlatform expectedCloudPlatform = generateCloudPlatform();

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));
        when(cloudPlatformRepository.save(any(CloudPlatform.class))).thenReturn(expectedCloudPlatform);

        CloudPlatform actualCloudPlatform = cloudPlatformService.addCloudPlatform(userEmail, platformName, accessToken, refreshToken, null);

        assertEquals(expectedCloudPlatform, actualCloudPlatform);
        verify(userEntityRepository, times(1)).save(userEntity);
    }

    @Test
    void deleteCloudPlatform_oneDrive() {
        String userEmail = "email@example.com";
        String platformName = "OneDrive";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));

        cloudPlatformService.deleteCloudPlatform(userEmail, platformName);

        verify(userEntityRepository, times(1)).save(userEntity);
        verify(cloudPlatformRepository, times(1)).deleteByUserEntityEmailAndPlatformName(userEmail, platformName);
    }

    @Test
    void deleteCloudPlatform_googleDrive() {
        String userEmail = "email@example.com";
        String platformName = "GoogleDrive";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));

        cloudPlatformService.deleteCloudPlatform(userEmail, platformName);

        verify(userEntityRepository, times(1)).save(userEntity);
        verify(cloudPlatformRepository, times(1)).deleteByUserEntityEmailAndPlatformName(userEmail, platformName);
    }

    @Test
    void deleteCloudPlatform_unsupportedDrive_throwsException() {
        String userEmail = "email@example.com";
        String platformName = "provider";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));
        assertThrows(RuntimeException.class, () -> cloudPlatformService.deleteCloudPlatform(userEmail, platformName));

        verify(userEntityRepository, times(0)).save(userEntity);
        verify(cloudPlatformRepository, times(0)).deleteByUserEntityEmailAndPlatformName(userEmail, platformName);
    }

    @Test
    void getUserCloudPlatform_returnsCloudPlatform() {
        String userEmail = "email@example.com";
        String platformName = "provider";

        CloudPlatform cloudPlatform = new CloudPlatform();

        when(cloudPlatformRepository.findByUserEntityEmailAndPlatformName(userEmail, platformName)).thenReturn(cloudPlatform);
        cloudPlatformService.getUserCloudPlatform(userEmail, platformName);

        verify(cloudPlatformRepository, times(1)).findByUserEntityEmailAndPlatformName(userEmail, platformName);
    }
}