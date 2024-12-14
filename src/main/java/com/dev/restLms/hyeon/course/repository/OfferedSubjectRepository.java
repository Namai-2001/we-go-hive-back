package com.dev.restLms.hyeon.course.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.OfferedSubjects;

@Repository
public interface OfferedSubjectRepository extends JpaRepository<OfferedSubjects, String> {
	OfferedSubjects findByofferedSubjectsId(String offeredSubjectsId);
}
