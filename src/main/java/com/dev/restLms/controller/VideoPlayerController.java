package com.dev.restLms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.DTO.BookMarkDTO;
import com.dev.restLms.DTO.VideoPlayerRunningTimeDTO;
import com.dev.restLms.model.VideoPlayerBookMark;
import com.dev.restLms.model.VideoPlayerOfferedSubjects;
import com.dev.restLms.model.VideoPlayerSubjectOwnVideo;
import com.dev.restLms.model.VideoPlayerUser;
import com.dev.restLms.model.VideoPlayerUserOwnSubjectVideo;
import com.dev.restLms.model.VideoPlayerVideo;
import com.dev.restLms.persistent.VideoPlayerBookMarkRepository;
import com.dev.restLms.persistent.VideoPlayerOfferedSubjectsRepository;
import com.dev.restLms.persistent.VideoPlayerSubjectOwnVideoRepository;
import com.dev.restLms.persistent.VideoPlayerUserOwnSubjectVideoRepository;
import com.dev.restLms.persistent.VideoPlayerUserRepository;
import com.dev.restLms.persistent.VideoPlayerVideoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
      @Parameter(description = "사용자 고유 ID", required = true)
      @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true)
      @RequestParam Integer episodeId,
      @Parameter(description = "개설 과목 코드", required = true)
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
      
    @Operation(summary = "강의 플레이어에 들어갈 북마크 목록")
    @GetMapping("/bookmarkList")
    public List<VideoPlayerBookMark> getBookMarkList (
      @Parameter(description = "사용자 고유 ID", required = true)
      @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true)
      @RequestParam Integer episodeId,
      @Parameter(description = "개설 과목 코드", required = true)
      @RequestParam String offeredSubjectsId) {
        return videoPlayerBookMarkRepository.findByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(sessionId,episodeId,offeredSubjectsId);
    }

    @Operation(summary = "북마크 추가(20개가 넘으면 사이즈 반환, 동일한 시점의 북마크 불가)")
    @PostMapping("/addBookmark")
    public String addBookmark (
      @Parameter(description = "사용자 고유 ID", required = true)
      @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true)
      @RequestParam Integer episodeId,
      @Parameter(description = "개설 과목 코드", required = true)
      @RequestParam String offeredSubjectsId,
      @RequestBody BookMarkDTO bookmarkDTO) {
      try {
        List<VideoPlayerBookMark> videoPlayerBookMark = videoPlayerBookMarkRepository.findByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(sessionId, episodeId, offeredSubjectsId);
        // 한 과목당 학생은 북마크 20개 이하로 추가 가능함
        // 이미 있는 북마크 시간에 대해서는 추가 불가능
        System.out.println(bookmarkDTO.getBookmarkContent());
        System.out.println(bookmarkDTO.getBookmarkTime());
        if (videoPlayerBookMark.size() <= 20 && bookmarkDTO.getBookmarkTime() != null) {
          if(videoPlayerBookMarkRepository.findByBookmarkTime(bookmarkDTO.getBookmarkTime()).isPresent()) return "Duplicated Time";
            VideoPlayerBookMark bookMark = VideoPlayerBookMark.builder()
              .bmEpisodeId(episodeId)
              .bmSessionId(sessionId)
              .bmOfferedSubjectsId(offeredSubjectsId)
              .bookmarkTime(bookmarkDTO.getBookmarkTime())
              .bookmarkContent(bookmarkDTO.getBookmarkContent())
              .build();
           
            videoPlayerBookMarkRepository.save(bookMark);
        }
        return "Success";
      } catch (HttpMessageNotReadableException e) {
          // JSON 파싱 에러 처리 - 특수문자(이스케이프 문자 등)
          System.out.println(e.getMessage());
          return "Special characters wrong";
      } catch (Exception e) {
          System.out.println(e.getMessage());
          return "Something wrong";
      }
    }

    @Operation(summary = "처음 비디오 플레이어 실행시에 실행될 강의 데이터")
    @GetMapping("/runningVideo")
    public Map<String, Object> playVideo(
      @Parameter(description = "사용자 고유 ID", required = true)
      @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true)
      @RequestParam Integer episodeId,
      @Parameter(description = "개설 과목 코드", required = true)
      @RequestParam String offeredSubjectsId) {
        Map<String, Object> resultMap = new HashMap<>();
        Optional<VideoPlayerUserOwnSubjectVideo> userOwnSubjectVideo = videoPlayerUserOwnSubjectVideoRepository.findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsid(sessionId, episodeId, offeredSubjectsId);
        Optional<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideo = videoPlayerSubjectOwnVideoRepository.findBySovOffredSubjectsIdAndEpisodeId(userOwnSubjectVideo.get().getUosvOfferedSubjectsid(), userOwnSubjectVideo.get().getUosvEpisodeId());
        Optional<VideoPlayerOfferedSubjects> videoPlayerOfferedSubjects = videoPlayerOfferedSubjectsRepository.findById(offeredSubjectsId);
        Optional<VideoPlayerUser> videoPlayerUser = videoPlayerUserRepository.findBySessionId(videoPlayerOfferedSubjects.get().getTeacherSessionId());
        Optional<VideoPlayerVideo> videoPlayerVideo = videoPlayerVideoRepository.findByVideoId(videoPlayerSubjectOwnVideo.get().getSovVideoId());
        
        resultMap.put("final", userOwnSubjectVideo.get().getFinalLocation());
        resultMap.put("teacherName", videoPlayerUser.get().getUserName());
        resultMap.put("videoLink", videoPlayerVideo.get().getVideoLink());
        resultMap.put("vidTitle", videoPlayerVideo.get().getVideoTitle());
        return resultMap;
    }

    @Operation(summary = "비디오 플레이어 FINAL 수정 & progress 업데이트")
    @PostMapping("/UpdateFinal")
    public String updateFinal(
      @Parameter(description = "사용자 고유 ID", required = true)
      @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true)
      @RequestParam Integer episodeId,
      @Parameter(description = "개설 과목 코드", required = true)
      @RequestParam String offeredSubjectsId,
      @RequestBody VideoPlayerRunningTimeDTO runningTimeDTO) {
        Optional<VideoPlayerUserOwnSubjectVideo> userOwnSubjectVideo = videoPlayerUserOwnSubjectVideoRepository.findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsid(sessionId, episodeId, offeredSubjectsId);
        if (userOwnSubjectVideo.isEmpty()) {
          return "UserOwnSubjectVideo not found";
        }
        Optional<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideo = videoPlayerSubjectOwnVideoRepository.findBySovOffredSubjectsIdAndEpisodeId(userOwnSubjectVideo.get().getUosvOfferedSubjectsid(), userOwnSubjectVideo.get().getUosvEpisodeId());
        Optional<VideoPlayerVideo> videoPlayerVideo = videoPlayerVideoRepository.findByVideoId(videoPlayerSubjectOwnVideo.get().getSovVideoId());

        // 특정 과목의 영상에서 영상의 최대 위치보다 유저가 현재 시청하고 있는 시점이 미치지 못한 경우 -> 업데이트
        // 기존에 시청했던 위치보다 더 앞의 위치를 시청하는 경우 -> 업데이트
        if(videoPlayerVideo.get().getMax() > userOwnSubjectVideo.get().getFinalLocation() && userOwnSubjectVideo.get().getFinalLocation() < runningTimeDTO.getFinalLocation()){
          userOwnSubjectVideo.get().setFinalLocation(runningTimeDTO.getFinalLocation());
          // 해당 과목에 대한 영상 진행도 업데이트
          userOwnSubjectVideo.get().setProgress((int) Math.ceil(runningTimeDTO.getFinalLocation() / (double) videoPlayerVideo.get().getMax() * 100));
          videoPlayerUserOwnSubjectVideoRepository.save(userOwnSubjectVideo.get());
        }else return "Aleady Final";
        return "Update";
    }


}