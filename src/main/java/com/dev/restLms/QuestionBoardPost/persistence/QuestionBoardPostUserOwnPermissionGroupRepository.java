package com.dev.restLms.QuestionBoardPost.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostUserOwnPermissionGroup;
import java.util.Optional;


@Repository
public interface QuestionBoardPostUserOwnPermissionGroupRepository extends JpaRepository<QuestionBoardPostUserOwnPermissionGroup, String> {
    Optional<QuestionBoardPostUserOwnPermissionGroup> findBySessionId(String sessionId);
}
