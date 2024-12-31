package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;

public interface TV_SOV2_Repository extends JpaRepository<SubjectOwnVideo, String>{
    Optional<SubjectOwnVideo> findBySovOfferedSubjectsIdAndSovVideoId(String sovOfferedSubjectsId, String sovVideoId);
}

