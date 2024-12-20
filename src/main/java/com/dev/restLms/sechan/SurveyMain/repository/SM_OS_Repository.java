package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;

public interface SM_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    // 'offeredSubjectsId'를 기준으로 검색
    List<OfferedSubjects> findByOfferedSubjectsIdIn(List<String> offeredSubjectsIds);
}
