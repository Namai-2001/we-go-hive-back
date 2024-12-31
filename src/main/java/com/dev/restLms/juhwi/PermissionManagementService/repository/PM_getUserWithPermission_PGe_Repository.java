package com.dev.restLms.juhwi.PermissionManagementService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.juhwi.PermissionManagementService.projection.PM_getUserWithPermission_PGe_Projection;



public interface PM_getUserWithPermission_PGe_Repository extends JpaRepository<PermissionGroup, String> {
    Page<PM_getUserWithPermission_PGe_Projection> findByPermissionName(String permissionName, Pageable pageable);
}