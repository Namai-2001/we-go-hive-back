package com.dev.restLms.QuestionBoard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoard.model.QuestionBoardCourseOwnSubject;
import java.util.Optional;


@Repository
public interface QuestionBoardCourseOwnSubjectRepository extends JpaRepository<QuestionBoardCourseOwnSubject, String> {
    Optional<QuestionBoardCourseOwnSubject> findBySubjectId(String subjectId);
}
