package com.dev.restLms.announcementPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.Optional;


public interface AnnouncementPostUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String> {

    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
    
}
