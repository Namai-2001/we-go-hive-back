package com.dev.restLms.QuestionBoard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoard.model.QuestionBoardOfferedSubjects;
import java.util.Optional;



@Repository
public interface QuestionBoardOfferedSubjectsRepository extends JpaRepository<QuestionBoardOfferedSubjects, String> {
    Optional<QuestionBoardOfferedSubjects> findByOfferedSubjectsId(String offeredSubjectsId);
    Optional<QuestionBoardOfferedSubjects> findByOfferedSubjectsIdAndOfficerSessionId(String offeredSubjectsId, String officerSessionId);
    Optional<QuestionBoardOfferedSubjects> findByOfferedSubjectsIdAndTeacherSessionId(String offeredSubjectsId, String teacherSessionId);
}
