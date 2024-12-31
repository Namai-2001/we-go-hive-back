package com.dev.restLms.hyeon.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;

public interface SubjectOwnVideoRepository extends JpaRepository<SubjectOwnVideo, String> {
   SubjectOwnVideo findByEpisodeIdAndSovOfferedSubjectsId(String episodeId, String sovOfferedSubjectsId);
}