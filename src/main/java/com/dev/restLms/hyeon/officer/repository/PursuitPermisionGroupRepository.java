package com.dev.restLms.hyeon.officer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.hyeon.officer.projection.PursuitPermissionGroup;

public interface PursuitPermisionGroupRepository extends JpaRepository<PermissionGroup, String> {
    PursuitPermissionGroup findByPermissionGroupUuid(String permissionGroupUuid);
    PursuitPermissionGroup findByPermissionName(String permissionName);
}