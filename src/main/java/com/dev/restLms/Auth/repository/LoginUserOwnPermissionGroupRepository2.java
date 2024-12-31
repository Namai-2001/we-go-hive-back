package com.dev.restLms.Auth.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.Auth.projection.permissionUuidProjection;
import com.dev.restLms.entity.UserOwnPermissionGroup;

public interface LoginUserOwnPermissionGroupRepository2 extends JpaRepository<UserOwnPermissionGroup, String>{
    Optional<permissionUuidProjection> findBySessionId(String sessionId);
}