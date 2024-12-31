package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface OS_Repository extends JpaRepository<OfferedSubjects, String> {
    // 개설 과목 ID로 과목 ID 조회
    Optional<OfferedSubjects> findByOfferedSubjectsId(String offeredSubjectsId);
    
}
