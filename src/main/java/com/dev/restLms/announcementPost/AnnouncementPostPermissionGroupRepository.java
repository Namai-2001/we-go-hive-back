package com.dev.restLms.announcementPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import java.util.Optional;


public interface AnnouncementPostPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<announcementPostPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
