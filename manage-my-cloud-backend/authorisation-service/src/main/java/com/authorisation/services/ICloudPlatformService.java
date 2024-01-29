package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;

public interface ICloudPlatformService {

    CloudPlatform addCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken);

    void deleteCloudPlatform(String userEmail, String platformName);
}
