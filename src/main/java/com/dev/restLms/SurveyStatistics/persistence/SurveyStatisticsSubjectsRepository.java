package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSubjects;
import com.dev.restLms.entity.Subject;
import java.util.Optional;


public interface SurveyStatisticsSubjectsRepository extends JpaRepository<Subject, String> {
    Optional<SurveyStatisticsSubjects> findBySubjectId(String subjectId);
}
