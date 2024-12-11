package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerBookMark;
import java.util.List;
import java.util.Optional;



@Repository
public interface VideoPlayerBookMarkRepository extends JpaRepository<VideoPlayerBookMark, Object> {
  List<VideoPlayerBookMark> findAllByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(String bmSessionId, Integer bmEpisodeId, String bmOfferedSubjectsId);
  Optional<VideoPlayerBookMark> findByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsIdAndBookmarkTime(String bmSessionId, Integer bmEpisodeId, String bmOfferedSubjectsId, Integer bookmarkTime);
  Optional<VideoPlayerBookMark> findByBookmarkTime(Integer bookmarkTime);
}
