package com.dev.restLms.SubjectApproval.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SubjectApproval.projection.SubjectApprovalSubject;
import com.dev.restLms.entity.Subject;
import java.util.List;
import java.util.Optional;


public interface SubjectApprovalSubjectRepository extends JpaRepository<Subject, String> {
    List<SubjectApprovalSubject> findBySubjectNameContaining(String subjectName, Sort sort);

    Optional<SubjectApprovalSubject> findBySubjectId(String subjectId);
}
