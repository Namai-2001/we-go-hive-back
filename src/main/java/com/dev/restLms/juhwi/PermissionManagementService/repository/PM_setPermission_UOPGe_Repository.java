package com.dev.restLms.juhwi.PermissionManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.juhwi.PermissionManagementService.projection.PM_setPermission_UOPGe_Projection;

import java.util.List;
import java.util.Optional;


public interface PM_setPermission_UOPGe_Repository extends JpaRepository<UserOwnPermissionGroup, String>{
    Optional<List<PM_setPermission_UOPGe_Projection>> findBySessionId(String sessionId);
}
