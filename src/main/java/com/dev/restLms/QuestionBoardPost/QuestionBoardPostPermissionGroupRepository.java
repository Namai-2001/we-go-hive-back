package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.PermissionGroup;

import java.util.Optional;


@Repository
public interface QuestionBoardPostPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<QuestionBoardPostPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
