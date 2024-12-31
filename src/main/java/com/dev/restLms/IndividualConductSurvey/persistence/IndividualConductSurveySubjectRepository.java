package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveySubject;
import com.dev.restLms.entity.Subject;
import java.util.Optional;
import java.util.List;



public interface IndividualConductSurveySubjectRepository extends JpaRepository<Subject, String> {
    Optional<IndividualConductSurveySubject> findBySubjectId(String subjectId);
    List<IndividualConductSurveySubject> findBySubjectNameContaining(String subjectName, Sort sort);
}
