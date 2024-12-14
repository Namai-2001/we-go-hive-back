package com.dev.restLms.QuestionBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.PermissionGroup;

import java.util.Optional;


@Repository
public interface QuestionBoardPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {

    Optional<QuestionBoardPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
    
}
