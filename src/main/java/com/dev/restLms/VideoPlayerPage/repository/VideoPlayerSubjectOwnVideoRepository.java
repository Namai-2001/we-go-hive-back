package com.dev.restLms.VideoPlayerPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.VideoPlayerPage.projection.VideoPlayerSubjectOwnVideo;
import com.dev.restLms.entity.SubjectOwnVideo;

import java.util.Optional;
import java.util.List;

@Repository
public interface VideoPlayerSubjectOwnVideoRepository extends JpaRepository<SubjectOwnVideo, String> {
  List<VideoPlayerSubjectOwnVideo> findBySovOfferedSubjectsId(String sovOffredSubjectsId);
  Optional<VideoPlayerSubjectOwnVideo> findBySovOfferedSubjectsIdAndEpisodeId(String sovOffredSubjectsId, String episodeId);
}
