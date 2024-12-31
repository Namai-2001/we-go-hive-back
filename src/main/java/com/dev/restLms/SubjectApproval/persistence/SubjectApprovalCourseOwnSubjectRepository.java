package com.dev.restLms.SubjectApproval.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SubjectApproval.projection.SubjectApprovalCourseOwnSubject;
import com.dev.restLms.entity.CourseOwnSubject;
import java.util.Optional;




public interface SubjectApprovalCourseOwnSubjectRepository extends JpaRepository<CourseOwnSubject, String> {
    Optional<SubjectApprovalCourseOwnSubject> findBySubjectIdAndSubjectApprovalAndOfficerSessionId(String subjectId, String subjectApproval, String officerSessionId);

    Optional<CourseOwnSubject> findByIncreaseIdAndSubjectIdAndOfficerSessionId(String increaseId, String subjectId,String officerSessionId);

    Optional<CourseOwnSubject> findByIncreaseId(String increaseId);
}
