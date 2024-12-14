package com.dev.restLms.VideoPlayerPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.VideoPlayerPage.projection.VideoPlayerBookMark;
import com.dev.restLms.entity.BookMark;

import java.util.List;
import java.util.Optional;



@Repository
public interface VideoPlayerBookMarkRepository extends JpaRepository<BookMark, Object> {
  List<VideoPlayerBookMark> findAllByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(String bmSessionId, String bmEpisodeId, String bmOfferedSubjectsId);
  Optional<VideoPlayerBookMark> findByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsIdAndBookmarkTime(String bmSessionId, String bmEpisodeId, String bmOfferedSubjectsId, String bookmarkTime);
  Optional<BookMark> findByIncreaseIdAndBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsIdAndBookmarkTime(String increaseId, String bmSessionId, String bmEpisodeId, String bmOfferedSubjectsId, String bookmarkTime);
  Optional<VideoPlayerBookMark> findByBookmarkTime(String bookmarkTime);
}
