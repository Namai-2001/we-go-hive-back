package com.dev.restLms.sechan.subjectInfoDetailPage.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_S_Projection;
import com.dev.restLms.entity.Subject;

public interface SID_S_Repository extends JpaRepository<Subject, String> {
    SID_S_Projection findFirstBySubjectId(String subjectId);
}