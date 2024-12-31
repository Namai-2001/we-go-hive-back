package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import java.util.Optional;


public interface FreeBulletinBoardPostPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<FreeBulletinBoardPostPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
