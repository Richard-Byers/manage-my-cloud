package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;
import com.authorisation.pojo.Account;
import com.authorisation.repositories.CloudPlatformRepository;
import com.authorisation.repositories.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.authorisation.givens.CloudPlatformGivens.generateCloudPlatform;
import static org.junit.jupiter.api.Assertions.*;
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
        String driveEmail = "email@example.com";
        String platformName = "OneDrive";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());
        userEntity.getLinkedAccounts().setLinkedDriveAccounts(new ArrayList<>(List.of(new Account(driveEmail, "OneDrive"))));

        CloudPlatform expectedCloudPlatform = generateCloudPlatform();

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));
        when(cloudPlatformRepository.save(any(CloudPlatform.class))).thenReturn(expectedCloudPlatform);

        CloudPlatform actualCloudPlatform = cloudPlatformService.addCloudPlatform(userEmail, platformName, accessToken, refreshToken, null, driveEmail);

        assertEquals(expectedCloudPlatform, actualCloudPlatform);
        verify(userEntityRepository, times(1)).save(userEntity);
    }

    @Test
    void addCloudPlatform_LinkedAccountsNull() {
        String userEmail = "email@example.com";
        String driveEmail = "email@example.com";
        String platformName = "OneDrive";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);

        CloudPlatform expectedCloudPlatform = generateCloudPlatform();

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));
        when(cloudPlatformRepository.save(any(CloudPlatform.class))).thenReturn(expectedCloudPlatform);

        CloudPlatform actualCloudPlatform = cloudPlatformService.addCloudPlatform(userEmail, platformName, accessToken, refreshToken, null, driveEmail);

        assertEquals(expectedCloudPlatform, actualCloudPlatform);
        verify(userEntityRepository, times(1)).save(userEntity);
    }

    @Test
    void deleteCloudPlatform_oneDrive() {
        String userEmail = "email@example.com";
        String platformName = "OneDrive";
        String driveEmail = "email2@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());
        userEntity.getLinkedAccounts().setLinkedDriveAccounts(new ArrayList<>(List.of(new Account(driveEmail, "OneDrive"))));

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));

        cloudPlatformService.deleteCloudPlatform(userEmail, platformName, driveEmail);

        verify(userEntityRepository, times(1)).save(userEntity);
        verify(cloudPlatformRepository, times(1)).deleteByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
    }

    @Test
    void deleteCloudPlatform_NullLinkedAccounts() {
        String userEmail = "email@example.com";
        String platformName = "OneDrive";
        String driveEmail = "email2@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));

        cloudPlatformService.deleteCloudPlatform(userEmail, platformName, driveEmail);

        verify(userEntityRepository, times(0)).save(userEntity);
        verify(cloudPlatformRepository, times(0)).deleteByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
    }

    @Test
    void deleteCloudPlatform_googleDrive() {
        String userEmail = "email@example.com";
        String platformName = "GoogleDrive";
        String driveEmail = "email2@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());
        userEntity.getLinkedAccounts().setLinkedDriveAccounts(List.of(new Account(driveEmail, "OneDrive")));

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));

        cloudPlatformService.deleteCloudPlatform(userEmail, platformName, driveEmail);

        verify(userEntityRepository, times(1)).save(userEntity);
        verify(cloudPlatformRepository, times(1)).deleteByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
    }

    @Test
    void deleteCloudPlatform_unsupportedDrive_throwsException() {
        String userEmail = "email@example.com";
        String platformName = "provider";
        String driveEmail = "email2@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        when(userEntityRepository.findByEmail(userEmail)).thenReturn(java.util.Optional.of(userEntity));
        assertThrows(RuntimeException.class, () -> cloudPlatformService.deleteCloudPlatform(userEmail, platformName, driveEmail));

        verify(userEntityRepository, times(0)).save(userEntity);
        verify(cloudPlatformRepository, times(0)).deleteByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
    }

    @Test
    void getUserCloudPlatform_returnsCloudPlatform() {
        String userEmail = "email@example.com";
        String platformName = "provider";
        String driveEmail = "email2@example.com";

        CloudPlatform cloudPlatform = new CloudPlatform();

        when(cloudPlatformRepository.findByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail)).thenReturn(cloudPlatform);
        cloudPlatformService.getUserCloudPlatform(userEmail, platformName, driveEmail);

        verify(cloudPlatformRepository, times(1)).findByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
    }

    @Test
    void isDriveLinked_returnsTrue() {
        String userEmail = "email@example.com";
        String platformName = "provider";
        String driveEmail = "email2@example.com";

        CloudPlatform cloudPlatform = new CloudPlatform();

        when(cloudPlatformRepository.findByUserEntityEmailAndDriveEmailAndPlatformName(userEmail, platformName, driveEmail)).thenReturn(cloudPlatform);
        boolean driveLinked = cloudPlatformService.isDriveLinked(userEmail, platformName, driveEmail);

        assertTrue(driveLinked);
    }

    @Test
    void isDriveLinked_returnsFalse() {
        String userEmail = "email@example.com";
        String platformName = "provider";
        String driveEmail = "email2@example.com";

        when(cloudPlatformRepository.findByUserEntityEmailAndDriveEmailAndPlatformName(userEmail, platformName, driveEmail)).thenReturn(null);
        boolean driveLinked = cloudPlatformService.isDriveLinked(userEmail, platformName, driveEmail);

        assertFalse(driveLinked);
    }
}