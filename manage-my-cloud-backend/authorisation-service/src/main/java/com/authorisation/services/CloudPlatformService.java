package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;
import com.authorisation.pojo.Account;
import com.authorisation.repositories.CloudPlatformRepository;
import com.authorisation.repositories.UserEntityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;


import static com.authorisation.Constants.*;
import static com.authorisation.util.EncryptionUtil.encrypt;

@Service
@RequiredArgsConstructor
public class CloudPlatformService implements ICloudPlatformService {

    private final CloudPlatformRepository cloudPlatformRepository;
    private final UserEntityRepository userEntityRepository;

    public CloudPlatform addCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken, Date accessTokenExpiryDate, String driveEmail, boolean gaveGmailPermissions) {
        UserEntity userEntity = userEntityRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        LinkedAccounts linkedAccounts = userEntity.getLinkedAccounts();

        if (ONEDRIVE.equals(platformName) || GOOGLEDRIVE.equals(platformName)) {
            if (linkedAccounts == null) {
                linkedAccounts = new LinkedAccounts(0, new ArrayList<>());
                userEntity.setLinkedAccounts(linkedAccounts);
            }
            linkedAccounts.getLinkedDriveAccounts().add(new Account(driveEmail, platformName));
            linkedAccounts.setLinkedAccountsCount((linkedAccounts.getLinkedAccountsCount()) + 1);
        } else {
            throw new RuntimeException("Platform not supported");
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
        cloudPlatform.setDriveEmail(driveEmail);
        cloudPlatform.setGaveGmailPermissions(gaveGmailPermissions);

        return cloudPlatformRepository.save(cloudPlatform);
    }

    public CloudPlatform updateCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken, Date accessTokenExpiryDate, String driveEmail, boolean gaveGmailPermissions) {
        CloudPlatform cloudPlatform = cloudPlatformRepository.findByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
        if (cloudPlatform == null) {
            throw new RuntimeException("CloudPlatform not found");
        }

        String encryptedAccessToken = encrypt(accessToken);
        String encryptedRefreshToken = encrypt(refreshToken);

        cloudPlatform.setAccessToken(encryptedAccessToken);
        cloudPlatform.setRefreshToken(encryptedRefreshToken);
        cloudPlatform.setAccessTokenExpiryDate(accessTokenExpiryDate);
        cloudPlatform.setGaveGmailPermissions(gaveGmailPermissions);

        return cloudPlatformRepository.save(cloudPlatform);
    }

    @Transactional
    public void deleteCloudPlatform(String userEmail, String platformName, String driveEmail) {
        UserEntity userEntity = userEntityRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        LinkedAccounts linkedAccounts = userEntity.getLinkedAccounts();

        if (linkedAccounts == null) {
            return;
        }

        if (ONEDRIVE.equals(platformName) || GOOGLEDRIVE.equals(platformName)) {
            for (Account account : linkedAccounts.getLinkedDriveAccounts()) {
                if (account.getAccountType().equals(platformName) && account.getAccountEmail().equals(driveEmail)) {
                    linkedAccounts.getLinkedDriveAccounts().remove(account);
                    break;
                }
            }

            linkedAccounts.setLinkedAccountsCount((linkedAccounts.getLinkedAccountsCount()) - 1);
        } else {
            throw new RuntimeException("Platform not supported");
        }

        userEntityRepository.save(userEntity);

        cloudPlatformRepository.deleteByUserEntityEmailAndPlatformNameAndDriveEmail(userEntity.getEmail(), platformName, driveEmail);
    }

    boolean isDriveLinked(String userEmail, String driveEmail, String platformName) {
        CloudPlatform cloudPlatform = cloudPlatformRepository.findByUserEntityEmailAndDriveEmailAndPlatformName(userEmail, driveEmail, platformName);
        return cloudPlatform != null;
    }

    public CloudPlatform getUserCloudPlatform(String userEmail, String platformName, String driveEmail) {
        return cloudPlatformRepository.findByUserEntityEmailAndPlatformNameAndDriveEmail(userEmail, platformName, driveEmail);
    }

    public void saveCloudPlatform(CloudPlatform cloudPlatform) {
        cloudPlatformRepository.save(cloudPlatform);
    }

    public boolean isTokenRefreshNeeded(String userEmail, String platformName, String driveEmail) {
        CloudPlatform cloudPlatform = getUserCloudPlatform(userEmail, platformName, driveEmail);
        return cloudPlatform.getAccessTokenExpiryDate().getTime() - System.currentTimeMillis() <= EXPIRATION_THRESHOLD_MILLISECONDS;
    }

}