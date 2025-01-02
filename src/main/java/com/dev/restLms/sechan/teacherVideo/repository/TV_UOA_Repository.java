package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignment;

public interface TV_UOA_Repository extends JpaRepository<UserOwnAssignment, String> {
    List<UserOwnAssignment> findByOfferedSubjectsIdAndSubjectAcceptCategoryIn(String offeredSubjectsId, List<String> subjectAcceptCategory);
}
