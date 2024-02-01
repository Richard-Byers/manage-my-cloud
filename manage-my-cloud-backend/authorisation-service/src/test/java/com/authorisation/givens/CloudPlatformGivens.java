package com.authorisation.givens;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;

import java.util.Date;

import static com.authorisation.util.EncryptionUtil.encrypt;

public class CloudPlatformGivens {

    public static CloudPlatform generateCloudPlatform() {

        String userEmail = "email@example.com";
        String platformName = "OneDrive";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        CloudPlatform expectedCloudPlatform = new CloudPlatform();
        expectedCloudPlatform.setUserEntity(userEntity);
        expectedCloudPlatform.setPlatformName(platformName);
        expectedCloudPlatform.setAccessToken(accessToken);
        expectedCloudPlatform.setRefreshToken(refreshToken);

        return expectedCloudPlatform;
    }

    public static CloudPlatform generateCloudPlatformEncryptedTokens() {

        String userEmail = "email@example.com";
        String platformName = "OneDrive";
        String accessToken = encrypt("access_token");
        String refreshToken = encrypt("refresh_token");
        Date accessTokenExpiryDate = new Date();

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        userEntity.setLinkedAccounts(new LinkedAccounts());

        CloudPlatform expectedCloudPlatform = new CloudPlatform();
        expectedCloudPlatform.setUserEntity(userEntity);
        expectedCloudPlatform.setPlatformName(platformName);
        expectedCloudPlatform.setAccessToken(accessToken);
        expectedCloudPlatform.setRefreshToken(refreshToken);
        expectedCloudPlatform.setAccessTokenExpiryDate(accessTokenExpiryDate);


        return expectedCloudPlatform;
    }

}
