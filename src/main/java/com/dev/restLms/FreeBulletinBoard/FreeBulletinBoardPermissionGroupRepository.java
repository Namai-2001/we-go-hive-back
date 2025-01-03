package com.dev.restLms.FreeBulletinBoard;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import java.util.Optional;


public interface FreeBulletinBoardPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<PermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
