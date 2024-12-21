package com.dev.restLms.Auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.Auth.projection.permissionProjection;
import com.dev.restLms.entity.PermissionGroup;

public interface LoginUserPermissionRepository extends JpaRepository<PermissionGroup, String>{
    Optional<permissionProjection> findByPermissionGroupUuid(String permissionGroupUuid);
    
}
