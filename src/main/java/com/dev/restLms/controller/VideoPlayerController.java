package com.dev.restLms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.model.VideoPlayerBookMark;
import com.dev.restLms.model.VideoPlayerOfferedSubjects;
import com.dev.restLms.model.VideoPlayerSubjectOwnVideo;
import com.dev.restLms.model.VideoPlayerUserOwnSubjectVideo;
import com.dev.restLms.model.VideoPlayerVideo;
import com.dev.restLms.persistent.VideoPlayerBookMarkRepository;
import com.dev.restLms.persistent.VideoPlayerOfferedSubjectsRepository;
import com.dev.restLms.persistent.VideoPlayerSubjectOwnVideoRepository;
import com.dev.restLms.persistent.VideoPlayerUserOwnSubjectVideoRepository;
import com.dev.restLms.persistent.VideoPlayerUserRepository;
import com.dev.restLms.persistent.VideoPlayerVideoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
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
@Tag(name="VideoPlayer API", description = "강의 플레이어 API 목록" )
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

    // 엔드포인트 4개
    // 과목에 대한 강의 목록 ok
    // 해당 강의의 북마크 목록
    // 영상 불러오기
    // 책갈피 생성하기 및 삭제하기 및 수정하기

    // 강의 목록에 들어갈 내용임
    // (강의 썸네일 + 강의 회차가 제목 + 부제목 강의 명) -> 여러개로 조회되어야 함
    // video1 = {
    //    videoLink: "http://",
    //    episodeId: 1,
    //    episodeTitle: "Title"
    //}
    // video.getSovVideoId() // 해당 차시에 그 비디오 아이디 이거 가지고 링크랑 이미지랑 제목
    
    // 비디오 플레이어의 과목 목록에 들어갈 API
    @Operation(summary = "강의 플레이어에 들어갈 특정 사용자의  강의 목록")
    @GetMapping("/videoList")
    public List<Map<String, Object>> getSubjectsVideosList(
      @RequestParam String sessionId,
      @RequestParam Integer episodeId,
      @RequestParam String offeredSubjectsId) {
        
        List<Map<String, Object>> resultList = new ArrayList<>();

        // Optional 처리
        Optional<VideoPlayerUserOwnSubjectVideo>  userOwnSubjectVideo = videoPlayerUserOwnSubjectVideoRepository
                .findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsid(sessionId, episodeId, offeredSubjectsId);

        List<VideoPlayerSubjectOwnVideo> videoList = videoPlayerSubjectOwnVideoRepository
                .findBySovOffredSubjectsId(userOwnSubjectVideo.get().getUosvOfferedSubjectsid());

        for(VideoPlayerSubjectOwnVideo video : videoList){
          Map<String, Object> resultMap = new HashMap<>();
          Optional<VideoPlayerVideo> videoInfo =  videoPlayerVideoRepository.findByVideoId(video.getSovVideoId());
          resultMap.put("sortIdx", video.getVideoSortIndex());
          resultMap.put("vidLink", videoInfo.get().getVideoLink());
          resultMap.put("vidTitle", videoInfo.get().getVideoTitle());
          resultMap.put("vidImg", videoInfo.get().getVideoImg());
          resultList.add(resultMap);
        }
        return resultList;
    }
      
    @Operation(summary = "특정 사용자 북마크 목록으로 조회")
    @GetMapping("/bookmarkList")
    public List<VideoPlayerBookMark> getBookMarkList (
      @RequestParam String sessionId,
      @RequestParam Integer episodeId,
      @RequestParam String offeredSubjectsId) {
        return videoPlayerBookMarkRepository.findAll();
    }
}