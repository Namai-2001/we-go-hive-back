package com.dev.restLms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.model.VideoPlayerBookMark;
import com.dev.restLms.model.VideoPlayerOfferedSubjects;
import com.dev.restLms.persistent.VideoPlayerBookMarkRepository;
import com.dev.restLms.persistent.VideoPlayerOfferedSubjectsRepository;
import com.dev.restLms.persistent.VideoPlayerSubjectOwnVideoRepository;
import com.dev.restLms.persistent.VideoPlayerUserOwnSubjectVideoRepository;
import com.dev.restLms.persistent.VideoPlayerUserRepository;
import com.dev.restLms.persistent.VideoPlayerVideoRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@Tag(name="video-player-book-mark API", description = "복합키를 포함한 기본키에 대하여 반환함" )
@RequestMapping("/Videoplayer")
public class VideoPlayerController {

  @Autowired
  VideoPlayerBookMarkRepository videoPlayerBookMarkRepository;

  @Autowired
  VideoPlayerOfferedSubjectsRepository videoPlayerOfferedSubjectsRepository;

  @Autowired
  VideoPlayerUserOwnSubjectVideoRepository videoPlayerUserOwnSubjectVideoRepository;

  @Autowired
  VideoPlayerSubjectOwnVideoRepository videoPlayerSubjectOwnVideoRepository;

  @Autowired
  VideoPlayerVideoRepository videoPlayerVideoRepository;

  @Autowired
  VideoPlayerUserRepository videoPlayerUserRepository;

  @GetMapping("/GetAll")
  public List<VideoPlayerBookMark> findAllBookMarks() {
    return videoPlayerBookMarkRepository.findAll();
  }

@GetMapping("/specificTeacherId")
public ResponseEntity<Map<String, String>> findSpecificTeacherId(@RequestParam String offeredSubjectsId) {
    // Optional 처리
    Optional<VideoPlayerOfferedSubjects> optionalSubject = videoPlayerOfferedSubjectsRepository.findById(offeredSubjectsId);

    if (optionalSubject.isPresent()) {
        String teacherSessionId = optionalSubject.get().getTeacherSessionId();

        // 초기화 및 결과 설정
        Map<String, String> result = new HashMap<>();
        result.put("teacherSessionId", teacherSessionId);

        return ResponseEntity.ok(result); // 성공 시 200 OK 반환
    } else {
        // offeredSubjectsId에 해당하는 데이터가 없는 경우
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Subject not found"));
    }
}

  @GetMapping("/bookmark")
    public ResponseEntity<VideoPlayerBookMark> getBookmark(
            @RequestParam String bookmarkTime,
            @RequestParam String sessionId,
            @RequestParam int episodeId,
            @RequestParam String offeredSubjectsId) {

        // JPA Repository를 사용하여 데이터 검색
        VideoPlayerBookMark bookmark = videoPlayerBookMarkRepository
                .findByBookmarkTimeAndSessionIdAndEpisodeIdAndOfferedSubjectsId(
                        bookmarkTime, sessionId, episodeId, offeredSubjectsId);

        if (bookmark != null) {
            return ResponseEntity.ok(bookmark);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/specific-episode-bookmarks")
    public List<VideoPlayerBookMark> getSpecificBookmarks(
      @RequestParam String sessionId,
      @RequestParam int episodeId,
      @RequestParam String offeredSubjectsId) {
      return videoPlayerBookMarkRepository.findBySessionIdAndEpisodeIdAndOfferedSubjectsId(sessionId, episodeId, offeredSubjectsId);
    }

    // 강의 목록에 들어갈 내용임
    // 강의 섬네일 + 강의 회차가 제목 + 부제목 강의 명
    @GetMapping("/get-specific-user-own-subjects-videos")
    public String getSubjectVideosWithNameWithTeacherName(
      @RequestParam String sessionId,
      @RequestParam int episodeId,
      @RequestParam String offeredSubjectsId) {
        return null;
    }
}
