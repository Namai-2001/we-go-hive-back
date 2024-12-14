package com.dev.restLms.hyeon.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {
	Subject findBysubjectId(String subjectId);
}
