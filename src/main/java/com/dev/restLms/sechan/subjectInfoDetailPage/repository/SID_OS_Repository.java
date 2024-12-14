package com.dev.restLms.sechan.subjectInfoDetailPage.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface SID_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    Optional<OfferedSubjects> findBySubjectId(String subjectId);
}
    

