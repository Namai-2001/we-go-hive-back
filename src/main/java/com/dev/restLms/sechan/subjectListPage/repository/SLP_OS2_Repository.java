package com.dev.restLms.sechan.subjectListPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface SLP_OS2_Repository extends JpaRepository<OfferedSubjects, String>{
    List<OfferedSubjects> findBySubjectId(String SubjectId);
}
    

