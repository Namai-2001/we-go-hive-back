package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;

public interface SID_SOV_Repository extends JpaRepository<SubjectOwnVideo, String> {
    List<SubjectOwnVideo> findBySovOfferedSubjectsId(String offeredSubjectsId);
}
