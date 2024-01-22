package com.authorisation.repositories;

import com.authorisation.entities.CloudPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudPlatformRepository extends JpaRepository<CloudPlatform, Long> {
}
