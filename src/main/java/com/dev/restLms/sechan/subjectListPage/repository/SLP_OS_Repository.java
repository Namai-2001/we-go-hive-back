package com.dev.restLms.sechan.subjectListPage.repository;

import com.dev.restLms.entity.OfferedSubjects;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SLP_OS_Repository extends JpaRepository<OfferedSubjects, String> {
    // 모든 개설 과목 조회 (페이지네이션 지원)
    Page<OfferedSubjects> findAll(Pageable pageable);

    Page<OfferedSubjects> findBySubjectIdIn(List<String> subjectIdList, Pageable pageable);


    // 모든 개설 과목 중 개설 과목 명 조회 페이지네이션
    Page<OfferedSubjects> findByOfferedSubjectsIdIn(List<String> offeredSubjectsId,Pageable pageable);
    // 모든 개설 과목 중 강사 세션 ID로 조회 페이지네이션
    Page<OfferedSubjects> findByTeacherSessionIdIn(List<String> teacherSessionId,Pageable pageable);
    // 둘다 검색하는 페이지 네이션
    Page<OfferedSubjects> findByTeacherSessionIdInOrSubjectIdIn(List<String> teacherSessionId, List<String> offeredSubjectsId, Pageable pageable);
}
