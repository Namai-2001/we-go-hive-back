package com.dev.restLms.sechan.teacherAssignment.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignment;

public interface TA_UOA_Repository extends JpaRepository<UserOwnAssignment, String> {

    // 특정 과목(offeredSubjectsId)에 해당하는 사용자를 검색
    List<UserOwnAssignment> findByOfferedSubjectsIdAndSubjectAcceptCategoryIn(
            String offeredSubjectsId, List<String> subjectAcceptCategories);
}