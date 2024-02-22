package com.authorisation.services;

import com.authorisation.response.OneDriveTokenResponse;

public interface IOneDriveService {

    OneDriveTokenResponse getAndStoreUserTokens(String authCode, String email);
    void unlinkOneDrive(String email, String driveEmail);

}
