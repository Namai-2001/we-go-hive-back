package com.dev.restLms.IndividualSurveyStatistics.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSubject;
import com.dev.restLms.entity.Subject;
import java.util.List;
import java.util.Optional;


public interface IndividualSurveyStatisticsSubjectRepository extends JpaRepository<Subject, String> {
    
    List<IndividualSurveyStatisticsSubject> findBySubjectNameContaining(String subjectName, Sort sort);

    Optional<IndividualSurveyStatisticsSubject> findBySubjectId(String subjectId);

}
