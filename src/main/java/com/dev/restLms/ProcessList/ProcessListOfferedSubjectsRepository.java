package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.OfferedSubjects;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProcessListOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    List<ProcessListOfferedSubjects> findByCourseIdAndOfficerSessionIdAndSubjectId(String courseId, String officerSessionId, String subjectId);
    Optional<ProcessListOfferedSubjects> findBySubjectIdAndOfficerSessionId(String subjectId, String officerSessionId);
    Optional<ProcessListOfferedSubjects> findBySubjectIdAndOfficerSessionIdAndCourseId(String subjectId, String officerSessionId, String courseId);
}
