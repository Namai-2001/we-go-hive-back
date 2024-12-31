package com.dev.restLms.AssignmentPage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.AssignmentPage.projection.AssignmentPagesubjectNameProjection;
import com.dev.restLms.entity.Subject;

public interface AssignmentPageSubjectRepository extends JpaRepository<Subject, String>{
  Optional<AssignmentPagesubjectNameProjection> findBySubjectId(String subjectId);
}
