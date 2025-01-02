package com.dev.restLms.hyeon.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnPermissionGroup;

@Repository
public interface StudentUopgRepository extends JpaRepository<UserOwnPermissionGroup, String> {
    List<UserOwnPermissionGroup> findByPermissionGroupUuid2(String permissionGroupUuid2);
    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
}
