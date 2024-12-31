package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.Optional;


public interface FreeBulletinBoardPostUserOwnPermissionGroupRepository extends JpaRepository <UserOwnPermissionGroup, String> {
    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
}
