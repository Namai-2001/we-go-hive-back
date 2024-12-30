package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;

public interface CCP_SOV_Repository extends JpaRepository<SubjectOwnVideo, String> {
    List<SubjectOwnVideo> findBySovOfferedSubjectsId(String sovOfferedSubjectsId);
}
