package com.dev.restLms.QuestionBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.OfferedSubjects;

import java.util.Optional;



@Repository
public interface QuestionBoardOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    Optional<QuestionBoardOfferedSubjects> findByOfferedSubjectsId(String offeredSubjectsId);
    Optional<QuestionBoardOfferedSubjects> findByOfferedSubjectsIdAndOfficerSessionId(String offeredSubjectsId, String officerSessionId);
    Optional<QuestionBoardOfferedSubjects> findByOfferedSubjectsIdAndTeacherSessionId(String offeredSubjectsId, String teacherSessionId);
}
