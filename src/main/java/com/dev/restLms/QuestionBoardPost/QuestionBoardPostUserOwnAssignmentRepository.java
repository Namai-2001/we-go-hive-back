package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignment;
import java.util.Optional;


public interface QuestionBoardPostUserOwnAssignmentRepository extends JpaRepository<UserOwnAssignment, String> {
    Optional<QuestionBoardPostUserOwnAssignment> findByUserSessionIdAndOfferedSubjectsId(String userSessionId, String offeredSubjectsId);
}
