package com.dev.restLms.juhwi.PermissionManagementService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.juhwi.PermissionManagementService.projection.PM_getUserWithPermission_UOPGe_projection;

public interface PM_getUserWithPermission_UOPGe_Repository extends JpaRepository<UserOwnPermissionGroup, String> {
    Page<PM_getUserWithPermission_UOPGe_projection> findByPermissionGroupUuid2(String permissionGroupUuid2, Pageable pageable);
}
