package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Board;
import java.util.Optional;



public interface QuestionBoardPostBoardRepository extends JpaRepository<Board, String> {
    Optional<QuestionBoardPostBoard> findByBoardId(String boardId);
    Optional<QuestionBoardPostBoard> findByTeacherSessionIdAndOfferedSubjectsId(String teacherSessionId, String offeredSubjectsId);
}
