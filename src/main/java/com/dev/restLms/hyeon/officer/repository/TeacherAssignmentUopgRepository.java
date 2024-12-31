package com.dev.restLms.hyeon.officer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnPermissionGroup;

@Repository
public interface TeacherAssignmentUopgRepository extends JpaRepository<UserOwnPermissionGroup, String> {
    List<UserOwnPermissionGroup> findByPermissionGroupUuid2(String permissionGroupUuid2);
    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
}
