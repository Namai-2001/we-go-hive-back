package com.dev.restLms.sechan.subjectListPage.repository;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SLP_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    // 모든 개설 과목 조회
    List<OfferedSubjects> findAll();
}