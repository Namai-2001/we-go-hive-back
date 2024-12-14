package com.dev.restLms.QuestionBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.Subject;

import java.util.Optional;


@Repository
public interface QuestionBoardSubjectRepository extends JpaRepository<Subject, String> {
    Optional<QuestionBoardSubject> findBySubjectId(String subjectId);
}
