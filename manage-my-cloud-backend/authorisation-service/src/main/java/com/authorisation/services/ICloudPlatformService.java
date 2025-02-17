package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;

import java.util.Date;

public interface ICloudPlatformService {

    CloudPlatform addCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken, Date accessTokenExpiryDate, String driveEmail, boolean gaveGmailPermissions);
    CloudPlatform updateCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken, Date accessTokenExpiryDate, String driveEmail, boolean gaveGmailPermissions);

    void deleteCloudPlatform(String userEmail, String platformName, String driveEmail);
}
