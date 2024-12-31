package com.dev.restLms.juhwi.PermissionManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import java.util.List;


public interface PM_setPermission_PGe_Repository extends JpaRepository<PermissionGroup, String>{
    List<PermissionGroup> findByPermissionName(String permissionName);
}