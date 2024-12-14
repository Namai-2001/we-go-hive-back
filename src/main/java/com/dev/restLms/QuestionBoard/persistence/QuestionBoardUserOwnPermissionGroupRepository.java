package com.dev.restLms.QuestionBoard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoard.model.QuestionBoardUserOwnPermissionGroup;
import java.util.Optional;


@Repository
public interface QuestionBoardUserOwnPermissionGroupRepository extends JpaRepository<QuestionBoardUserOwnPermissionGroup, String> {

    Optional<QuestionBoardUserOwnPermissionGroup> findBySessionId(String sessionId);
    
}
