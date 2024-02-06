package com.authorisation.repositories;

import com.authorisation.entities.CloudPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudPlatformRepository extends JpaRepository<CloudPlatform, Long> {

    void deleteByUserEntityEmailAndPlatformName(String email, String platformName);

    CloudPlatform findByUserEntityEmailAndPlatformName(String email, String platformName);

}
