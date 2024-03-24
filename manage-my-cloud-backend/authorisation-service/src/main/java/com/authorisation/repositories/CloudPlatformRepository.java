package com.authorisation.repositories;

import com.authorisation.entities.CloudPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CloudPlatformRepository extends JpaRepository<CloudPlatform, Long> {

    void deleteByUserEntityEmailAndPlatformNameAndDriveEmail(String email, String platformName, String driveEmail);
    void deleteByUserEntityEmailAndPlatformName(String email, String platformName);
    CloudPlatform findByUserEntityEmailAndPlatformName(String email, String platformName);
    List<CloudPlatform> findAllByUserEntityEmail(String email);
    CloudPlatform findByUserEntityEmailAndPlatformNameAndDriveEmail(String email, String platformName, String driveEmail);
    CloudPlatform findByUserEntityEmailAndDriveEmailAndPlatformName(String email, String driveEmail, String platformName);

}
