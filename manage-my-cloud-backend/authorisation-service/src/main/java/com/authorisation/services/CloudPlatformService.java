package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;
import com.authorisation.repositories.CloudPlatformRepository;
import com.authorisation.repositories.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.authorisation.Constants.GOOGLEDRIVE;
import java.util.Date;

import static com.authorisation.Constants.ONEDRIVE;
import static com.authorisation.util.EncryptionUtil.encrypt;

@Service
@RequiredArgsConstructor
public class CloudPlatformService implements ICloudPlatformService {

    private final CloudPlatformRepository cloudPlatformRepository;
    private final UserEntityRepository userEntityRepository;

    public CloudPlatform addCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken, Date accessTokenExpiryDate) {
        UserEntity userEntity = userEntityRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        LinkedAccounts linkedAccounts = userEntity.getLinkedAccounts();

        if (ONEDRIVE.equals(platformName) || GOOGLEDRIVE.equals(platformName)) {
            if (linkedAccounts == null) {
                linkedAccounts = new LinkedAccounts();
                userEntity.setLinkedAccounts(linkedAccounts);
            }
            if (ONEDRIVE.equals(platformName)) {
                linkedAccounts.setOneDrive(true);
                linkedAccounts.setLinkedAccountsCount(linkedAccounts.getLinkedAccountsCount() + 1);
            } else {
                linkedAccounts.setGoogleDrive(true);
                linkedAccounts.setLinkedAccountsCount(linkedAccounts.getLinkedAccountsCount() + 1);
            }
        }
        userEntityRepository.save(userEntity);

        String encryptedAccessToken = encrypt(accessToken);
        String encryptedRefreshToken = encrypt(refreshToken);

        CloudPlatform cloudPlatform = new CloudPlatform();
        cloudPlatform.setUserEntity(userEntity);
        cloudPlatform.setPlatformName(platformName);
        cloudPlatform.setAccessToken(encryptedAccessToken);
        cloudPlatform.setRefreshToken(encryptedRefreshToken);
        cloudPlatform.setAccessTokenExpiryDate(accessTokenExpiryDate);

        return cloudPlatformRepository.save(cloudPlatform);
    }

    @Transactional
    public void deleteCloudPlatform(String userEmail, String platformName) {
        UserEntity userEntity = userEntityRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        LinkedAccounts linkedAccounts = userEntity.getLinkedAccounts();

        if (ONEDRIVE.equals(platformName)) {
            if (linkedAccounts == null) {
                linkedAccounts = new LinkedAccounts();
                userEntity.setLinkedAccounts(linkedAccounts);
            }
            linkedAccounts.setOneDrive(false);
            linkedAccounts.setLinkedAccountsCount(linkedAccounts.getLinkedAccountsCount() - 1);
        } else if (GOOGLEDRIVE.equals(platformName)) {
            if (linkedAccounts == null) {
                linkedAccounts = new LinkedAccounts();
                userEntity.setLinkedAccounts(linkedAccounts);
            }
            linkedAccounts.setGoogleDrive(false);
            linkedAccounts.setLinkedAccountsCount(linkedAccounts.getLinkedAccountsCount() - 1);
        } else {
            throw new RuntimeException("Platform not supported");
        }

        userEntityRepository.save(userEntity);

        cloudPlatformRepository.deleteByUserEntityEmailAndPlatformName(userEntity.getEmail(), platformName);
    }

    public CloudPlatform getUserCloudPlatform(String userEmail, String platformName) {
        return cloudPlatformRepository.findByUserEntityEmailAndPlatformName(userEmail, platformName);
    }

}
