package com.dev.restLms.sechan.subjectListPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.sechan.subjectListPage.projection.S_Projection;

public interface SLP_S_Repository extends JpaRepository<Subject, String> {
    // S_Projection을 활용한 Subject 데이터 조회
    List<S_Projection> findBySubjectIdIn(List<String> subjectIds);
}