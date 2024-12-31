package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnSubjectVideo;

public interface TV_UOSV_Repository extends JpaRepository<UserOwnSubjectVideo, String> {
    List<UserOwnSubjectVideo> findByUosvOfferedSubjectsId(String offeredSubjectsId);
}
