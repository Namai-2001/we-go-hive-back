package com.dev.restLms.SubjectApproval.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface SubjectApprovalOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    
}
