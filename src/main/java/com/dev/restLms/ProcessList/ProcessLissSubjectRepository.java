package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;
import java.util.Optional;


public interface ProcessLissSubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findBySubjectId(String subjectId);
}
