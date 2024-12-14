package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.PermissionGroup;

import java.util.Optional;



@Repository
public interface ProcessListPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<ProcessListPermissionGroup> findByPermissionName(String permissionName);
    Optional<ProcessListPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
