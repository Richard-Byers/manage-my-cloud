package com.authorisation.repositories;

import com.authorisation.entities.CloudPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudPlatformRepository extends JpaRepository<CloudPlatform, Long> {

    void deleteByUserEntityEmailAndPlatformNameAndDriveEmail(String email, String platformName, String driveEmail);
    CloudPlatform findByUserEntityEmailAndPlatformName(String email, String platformName);
    CloudPlatform findByUserEntityEmailAndPlatformNameAndDriveEmail(String email, String platformName, String driveEmail);
    CloudPlatform findByUserEntityEmailAndDriveEmailAndPlatformName(String email, String driveEmail, String platformName);

}
