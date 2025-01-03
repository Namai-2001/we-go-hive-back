package com.dev.restLms.hyeon.officer.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.OfferedSubjects;

@Repository
public interface TeacherAssignmentOsRepository extends JpaRepository<OfferedSubjects, String> {
	List<OfferedSubjects> findByCourseIdAndOfficerSessionId(String courseId, String officerSessionId);
	Optional<OfferedSubjects> findByOfferedSubjectsId(String offeredSubjectsId);
}
