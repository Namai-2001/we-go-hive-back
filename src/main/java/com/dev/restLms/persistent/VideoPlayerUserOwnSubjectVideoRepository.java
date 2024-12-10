package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerUserOwnSubjectVideo;
import java.util.Optional;


@Repository
public interface VideoPlayerUserOwnSubjectVideoRepository extends JpaRepository<VideoPlayerUserOwnSubjectVideo, Object> {
  Optional<VideoPlayerUserOwnSubjectVideo> findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsid(String uosvSessionId, Integer uosvEpisodeId, String uosvOfferedSubjectsid);
}