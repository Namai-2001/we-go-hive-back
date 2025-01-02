package com.dev.restLms.hyeon.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.hyeon.officer.projection.PursuitPermissionGroup;

@Repository
public interface StudentPermisionGroupRepository extends JpaRepository<PermissionGroup, String> {
    PursuitPermissionGroup findByPermissionGroupUuid(String permissionGroupUuid);
    PursuitPermissionGroup findByPermissionName(String permissionName);
}