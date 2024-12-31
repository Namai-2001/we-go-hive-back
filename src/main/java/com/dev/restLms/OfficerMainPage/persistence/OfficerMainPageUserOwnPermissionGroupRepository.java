package com.dev.restLms.OfficerMainPage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.Optional;


public interface OfficerMainPageUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String>{
    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
}
