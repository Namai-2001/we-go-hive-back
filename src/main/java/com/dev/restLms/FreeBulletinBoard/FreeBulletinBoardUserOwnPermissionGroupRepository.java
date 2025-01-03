package com.dev.restLms.FreeBulletinBoard;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.Optional;


public interface FreeBulletinBoardUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String>{
    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
}
