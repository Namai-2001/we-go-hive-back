package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerSubjectOwnVideo;



import java.util.Optional;
import java.util.List;


@Repository
public interface VideoPlayerSubjectOwnVideoRepository extends JpaRepository<VideoPlayerSubjectOwnVideo, Integer> {
  List<VideoPlayerSubjectOwnVideo> findBySovOffredSubjectsId(String sovOffredSubjectsId);
  Optional<VideoPlayerSubjectOwnVideo> findBySovOffredSubjectsIdAndEpisodeId(String sovOffredSubjectsId, Integer episodeId);
}
