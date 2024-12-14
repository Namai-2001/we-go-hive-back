package com.dev.restLms.QuestionBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.UserOwnAssignment;

import java.util.Optional;


@Repository
public interface QuestionBoardUserOwnAssignmentRepository extends JpaRepository<UserOwnAssignment, Object> {
    Optional<QuestionBoardUserOwnAssignment> findByOfferedSubjectsIdAndUserSessionId(String offeredSubjectsId, String userSessionId);
}
