package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseOfferedSubjects;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.Optional;


public interface ModifyCourseOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    Optional<ModifyCourseOfferedSubjects> findByCourseIdAndOfficerSessionIdAndSubjectId(String courseId, String officerSessionId, String subjectId);
}
