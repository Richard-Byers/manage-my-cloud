package com.authorisation.services;

import com.authorisation.entities.CloudPlatform;
import com.authorisation.entities.LinkedAccounts;
import com.authorisation.entities.UserEntity;
import com.authorisation.repositories.CloudPlatformRepository;
import com.authorisation.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.authorisation.util.EncryptionUtil.encrypt;

@Service
@RequiredArgsConstructor
public class CloudPlatformService implements ICloudPlatformService {

    private final CloudPlatformRepository cloudPlatformRepository;
    private final UserEntityRepository userEntityRepository;

    public CloudPlatform addCloudPlatform(String userEmail, String platformName, String accessToken, String refreshToken) {
        UserEntity userEntity = userEntityRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        LinkedAccounts linkedAccounts = userEntity.getLinkedAccounts();

        if ("OneDrive".equals(platformName)) {
            if (linkedAccounts == null) {
                linkedAccounts = new LinkedAccounts();
                userEntity.setLinkedAccounts(linkedAccounts);
            }
            linkedAccounts.setOneDrive(true);
            linkedAccounts.setLinkedAccountsCount(linkedAccounts.getLinkedAccountsCount() + 1);
        }

        userEntityRepository.save(userEntity);

        String encryptedAccessToken = encrypt(accessToken);
        String encryptedRefreshToken = encrypt(refreshToken);

        CloudPlatform cloudPlatform = new CloudPlatform();
        cloudPlatform.setUserEntity(userEntity);
        cloudPlatform.setPlatformName(platformName);
        cloudPlatform.setAccessToken(encryptedAccessToken);
        cloudPlatform.setRefreshToken(encryptedRefreshToken);

        return cloudPlatformRepository.save(cloudPlatform);
    }

}
