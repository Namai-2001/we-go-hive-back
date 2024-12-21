package com.dev.restLms.VideoPlayerPage.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.VideoPlayerPage.DTO.BookMarkDTO;
import com.dev.restLms.VideoPlayerPage.DTO.VideoPlayerRunningTimeDTO;
import com.dev.restLms.VideoPlayerPage.projection.VideoPlayerBookMark;
import com.dev.restLms.VideoPlayerPage.projection.VideoPlayerSubjectOwnVideo;
import com.dev.restLms.VideoPlayerPage.projection.VideoPlayerUser;
import com.dev.restLms.VideoPlayerPage.repository.VideoPlayerBookMarkRepository;
import com.dev.restLms.VideoPlayerPage.repository.VideoPlayerOfferedSubjectsRepository;
import com.dev.restLms.VideoPlayerPage.repository.VideoPlayerSubjectOwnVideoRepository;
import com.dev.restLms.VideoPlayerPage.repository.VideoPlayerUserOwnSubjectVideoRepository;
import com.dev.restLms.VideoPlayerPage.repository.VideoPlayerUserRepository;
import com.dev.restLms.VideoPlayerPage.repository.VideoPlayerVideoRepository;
import com.dev.restLms.entity.BookMark;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.entity.Video;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "VideoPlayer API", description = "강의 플레이어 API 목록")
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

  @GetMapping("/specificTeacherId")
  public ResponseEntity<Map<String, String>> findSpecificTeacherId(@RequestParam String offeredSubjectsId) {
    // Optional 처리
    Optional<OfferedSubjects> optionalSubject = videoPlayerOfferedSubjectsRepository.findById(offeredSubjectsId);

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
  // videoLink: "http://",
  // episodeId: 1,
  // episodeTitle: "Title"
  // }
  // video.getSovVideoId() // 해당 차시에 그 비디오 아이디 이거 가지고 링크랑 이미지랑 제목

  // 비디오 플레이어의 과목 목록에 들어갈 API
  @Operation(summary = "강의 플레이어에 들어갈 특정 사용자의  강의 목록")
  @GetMapping("/videoList")
  public List<Map<String, Object>> getSubjectsVideosList(
      @Parameter(description = "회차 번호", required = true) @RequestParam String episodeId,
      @Parameter(description = "개설 과목 코드", required = true) @RequestParam String offeredSubjectsId) {

    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
    final String userSessionId = auth.getPrincipal().toString();
    
    List<Map<String, Object>> resultList = new ArrayList<>();
    // Optional 처리
    Optional<UserOwnSubjectVideo> userOwnSubjectVideo = videoPlayerUserOwnSubjectVideoRepository
        .findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsId(userSessionId, episodeId, offeredSubjectsId);

    List<VideoPlayerSubjectOwnVideo> videoList = videoPlayerSubjectOwnVideoRepository
        .findBySovOfferedSubjectsId(userOwnSubjectVideo.get().getUosvOfferedSubjectsId());

    for (VideoPlayerSubjectOwnVideo video : videoList) {
      Map<String, Object> resultMap = new HashMap<>();
      Optional<Video> videoInfo = videoPlayerVideoRepository.findByVideoId(video.getSovVideoId());
      resultMap.put("sortIdx", video.getVideoSortIndex());
      resultMap.put("episodeId", video.getEpisodeId());
      resultMap.put("vidLink", videoInfo.get().getVideoLink());
      resultMap.put("vidTitle", videoInfo.get().getVideoTitle());
      resultMap.put("vidImg", videoInfo.get().getVideoImg());
      resultList.add(resultMap);
    }
    return resultList;
  }

  @Operation(summary = "강의 플레이어에 들어갈 북마크 목록")
  @GetMapping("/bookmarkList")
  public List<VideoPlayerBookMark> getBookMarkList(
      @Parameter(description = "사용자 고유 ID", required = true) @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true) @RequestParam String episodeId,
      @Parameter(description = "개설 과목 코드", required = true) @RequestParam String offeredSubjectsId) {
    return videoPlayerBookMarkRepository.findAllByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(sessionId, episodeId,
        offeredSubjectsId);
  }

  @Operation(summary = "특정 시점의 북마크 추가")
  @PostMapping("/addBookmark")
  public ResponseEntity<?> addBookmark(
      @Parameter(description = "사용자 고유 ID", required = true) @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true) @RequestParam String episodeId,
      @Parameter(description = "개설 과목 코드", required = true) @RequestParam String offeredSubjectsId,
      @RequestBody BookMarkDTO bookmarkDTO) {
    try {
      // 저장하려고 하는 북마크 시간이 제대로 들어오지 않으면, 반환
      if (bookmarkDTO.getBookmarkTime() != null) {
        BookMark bookMark = BookMark.builder()
            .bmEpisodeId(String.valueOf(episodeId))
            .bmSessionId(sessionId)
            .bmOfferedSubjectsId(offeredSubjectsId)
            .bookmarkTime(String.valueOf(bookmarkDTO.getBookmarkTime()))
            .bookmarkContent(bookmarkDTO.getBookmarkContent())
            .build();
        videoPlayerBookMarkRepository.save(bookMark);
        return ResponseEntity.ok(videoPlayerBookMarkRepository
            .findAllByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(sessionId, episodeId, offeredSubjectsId));
      }
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "잘못된 정보가 입력되었습니다.(특수문자 < > \\ / 등)");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    } catch (HttpMessageNotReadableException e) {
      // JSON 파싱 에러 처리 - 특수문자(이스케이프 문자 등)
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "북마크에 특수문자가 포함되면 안됩니다.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    } catch (Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "알 수 없는 오류가 발생했습니다.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
  }

  @Operation(summary = "특정 북마크 삭제")
  @PostMapping("/deleteBookmark")
  public ResponseEntity<?> deleteBookmark(
      @Parameter(description = "테이블 고유값", required = true) @RequestParam String increaseId,
      @Parameter(description = "사용자 SessionId", required = true) @RequestParam String sessionId,
      @Parameter(description = "특정 과목의 영상 회차 번호", required = true) @RequestParam String episodeId,
      @Parameter(description = "특정 개설 과목 번호", required = true) @RequestParam String offeredSubjectsId,
      @Parameter(description = "해당 북마크의 시점", required = true) @RequestParam String bookmarkTime) {
    // 특정 과목에 다른 회차에 동일한 시점의 북마크가 존재할 수 있기 떄문에 삭제시에는 increaseId를 포함한 검색을 통해
    // 삭제(Unique한 값임)
    Optional<BookMark> videoPlayerBookMark = videoPlayerBookMarkRepository
        .findByIncreaseIdAndBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsIdAndBookmarkTime(increaseId, sessionId,
            episodeId, offeredSubjectsId, bookmarkTime);
    if (videoPlayerBookMark.isPresent()) {
      videoPlayerBookMarkRepository.delete(videoPlayerBookMark.get());
      return ResponseEntity.ok(videoPlayerBookMarkRepository
          .findAllByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(sessionId, episodeId, offeredSubjectsId));
    }
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "Bookmark not found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @Operation(summary = "처음 비디오 플레이어 실행시에 실행될 강의 데이터")
  @GetMapping("/runningVideo")
  public ResponseEntity<?> playVideo(
      @Parameter(description = "사용자 고유 ID", required = true) @RequestParam String sessionId, // 45
      @Parameter(description = "회차 번호", required = true) @RequestParam String episodeId, // 14
      @Parameter(description = "개설 과목 코드", required = true) @RequestParam String offeredSubjectsId) { // o1a
    Map<String, Object> resultMap = new HashMap<>();
    Optional<UserOwnSubjectVideo> userOwnSubjectVideo = videoPlayerUserOwnSubjectVideoRepository
        .findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsId(sessionId, episodeId, offeredSubjectsId);
    List<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideoTotal = videoPlayerSubjectOwnVideoRepository
        .findBySovOfferedSubjectsId(offeredSubjectsId);
    Optional<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideo;
    Optional<OfferedSubjects> videoPlayerOfferedSubjects;
    Optional<VideoPlayerUser> videoPlayerUser;
    Optional<Video> videoPlayerVideo;
    // 해당 영상을 시청한 적이 있는 경우 => UserOwnSubjectVideo에서 해당 에피소드에 대한 정보가 있으며, 영상에서 시청 기록이
    // 0초 이상인 경우
    if (userOwnSubjectVideo.isPresent()) {
      if (Integer.parseInt(userOwnSubjectVideo.get().getUosvFinal()) > 0) {
        videoPlayerSubjectOwnVideo = videoPlayerSubjectOwnVideoRepository.findBySovOfferedSubjectsIdAndEpisodeId(
            userOwnSubjectVideo.get().getUosvOfferedSubjectsId(), userOwnSubjectVideo.get().getUosvEpisodeId());
        videoPlayerOfferedSubjects = videoPlayerOfferedSubjectsRepository.findById(offeredSubjectsId);
        videoPlayerUser = videoPlayerUserRepository
            .findBySessionId(videoPlayerOfferedSubjects.get().getTeacherSessionId());
        videoPlayerVideo = videoPlayerVideoRepository.findByVideoId(videoPlayerSubjectOwnVideo.get().getSovVideoId());

        resultMap.put("progress", userOwnSubjectVideo.get().getProgress());
        resultMap.put("final", userOwnSubjectVideo.get().getUosvFinal());
        resultMap.put("teacherName", videoPlayerUser.get().getUserName());
        resultMap.put("videoLink", videoPlayerVideo.get().getVideoLink());
        resultMap.put("vidTitle", videoPlayerVideo.get().getVideoTitle());
        return ResponseEntity.ok(resultMap);
      }
      // UserOwnSubjectVideo에 레코드가 없다는 것은
      // 이전 영상을 다 들었고 처음 들었을 때 => 영상 재생
      // 이전 영상을 듣지 않은 상태에서 이후의 영상을 참조하려고 할 때 => 제어
      // -- 이전 UserOwnSubjectVideo의 progress가 100이상이면 Ok sortIdx 값 -1 한 영상 항목의 값과 비교를
      // 해도 됨
      // 해당 영상이 처음인 경우 => 바로 삽입(수강을 신청할 때에 해당 과정을 듣는 사용자가 포함된 개설 과목에 대한 정보를 사용하여
      // UserOwnSubjectVideo 레코드도 finalLocation의 값이 0으로 삽입됨)
      // => 영상이 처음인 경우는 finalLocation값이 0(디폴트)이면서 해당 영상의 sortIndex값이 1이다(1회차)
      if (Integer.parseInt(userOwnSubjectVideo.get().getUosvFinal()) == 0 &&
          Integer.parseInt(videoPlayerSubjectOwnVideoRepository
              .findBySovOfferedSubjectsIdAndEpisodeId(offeredSubjectsId, episodeId).get().getVideoSortIndex()) == 1) {
        // sortIdx를 추출하기 위함
        // Optional<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideoCurrent =
        // videoPlayerSubjectOwnVideoRepository.findBySovOfferedSubjectsIdAndEpisodeId(offeredSubjectsId,
        // episodeId);
        // Integer inputSortIndex =
        // videoPlayerSubjectOwnVideoCurrent.get().getVideoSortIndex(); // 수강을 원하는 과목에
        // 대한 SortIndex
        // 해당 영상이 처음 강의인 경우 UserOwnSubjectVideo 레코드 정보 반환
        videoPlayerSubjectOwnVideo = videoPlayerSubjectOwnVideoRepository.findBySovOfferedSubjectsIdAndEpisodeId(
            userOwnSubjectVideo.get().getUosvOfferedSubjectsId(), userOwnSubjectVideo.get().getUosvEpisodeId());
        videoPlayerOfferedSubjects = videoPlayerOfferedSubjectsRepository.findById(offeredSubjectsId);
        videoPlayerUser = videoPlayerUserRepository
            .findBySessionId(videoPlayerOfferedSubjects.get().getTeacherSessionId());
        videoPlayerVideo = videoPlayerVideoRepository.findByVideoId(videoPlayerSubjectOwnVideo.get().getSovVideoId());
        resultMap.put("progress", userOwnSubjectVideo.get().getProgress());
        resultMap.put("final", userOwnSubjectVideo.get().getUosvFinal());
        resultMap.put("teacherName", videoPlayerUser.get().getUserName());
        resultMap.put("videoLink", videoPlayerVideo.get().getVideoLink());
        resultMap.put("vidTitle", videoPlayerVideo.get().getVideoTitle());
        return ResponseEntity.ok(resultMap);
      }

      // 1회차를 수강하는 것이 아닌 기존에 듣던 강의를 듣는 것이 아닌 새로운 강의로 넘어가는 경우
      // 보고자하는 영상보다 바로 전 회차의 progress값이 100 이상이어야 한다.
      else {
        Optional<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideoCurrent = videoPlayerSubjectOwnVideoRepository
            .findBySovOfferedSubjectsIdAndEpisodeId(offeredSubjectsId, episodeId);
        Integer inputSortIndex = Integer.parseInt(videoPlayerSubjectOwnVideoCurrent.get().getVideoSortIndex());
        for (VideoPlayerSubjectOwnVideo subjectOwnVideo : videoPlayerSubjectOwnVideoTotal) {
          // 전 과목과 비교
          if (Integer.parseInt(subjectOwnVideo.getVideoSortIndex()) == inputSortIndex - 1) {
            Optional<UserOwnSubjectVideo> prev = videoPlayerUserOwnSubjectVideoRepository
                .findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsId(sessionId, subjectOwnVideo.getEpisodeId(),
                    offeredSubjectsId);
            // 전 차시에 대한 레코드가 존재하고
            if (prev.isPresent() && Integer.parseInt(prev.get().getProgress()) >= 100) {
              // 해당 차시에 대한 진행도가 100이상인 경우(수강 완료 경우)
              videoPlayerSubjectOwnVideo = videoPlayerSubjectOwnVideoRepository.findBySovOfferedSubjectsIdAndEpisodeId(
                  userOwnSubjectVideo.get().getUosvOfferedSubjectsId(), userOwnSubjectVideo.get().getUosvEpisodeId());
              videoPlayerOfferedSubjects = videoPlayerOfferedSubjectsRepository.findById(offeredSubjectsId);
              videoPlayerUser = videoPlayerUserRepository
                  .findBySessionId(videoPlayerOfferedSubjects.get().getTeacherSessionId());
              videoPlayerVideo = videoPlayerVideoRepository
                  .findByVideoId(videoPlayerSubjectOwnVideo.get().getSovVideoId());
              resultMap.put("progress", userOwnSubjectVideo.get().getProgress());
              resultMap.put("final", userOwnSubjectVideo.get().getUosvFinal());
              resultMap.put("teacherName", videoPlayerUser.get().getUserName());
              resultMap.put("videoLink", videoPlayerVideo.get().getVideoLink());
              resultMap.put("vidTitle", videoPlayerVideo.get().getVideoTitle());
              return ResponseEntity.ok(resultMap);
            }
            // 이전 차시를 전부 수강하지 않은 경우
            else {
              Map<String, String> errorResponse = new HashMap<>();
              errorResponse.put("message", "이전 차시를 수강 완료 해주세요");
              return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
          }
        }
      }
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("message", "오류가 발생했습니다.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "오류가 발생했습니다.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @Operation(summary = "비디오 플레이어 FINAL 수정 & progress 업데이트")
  @PostMapping("/UpdateFinal")
  public Integer updateFinal(
      @Parameter(description = "사용자 고유 ID", required = true) @RequestParam String sessionId,
      @Parameter(description = "회차 번호", required = true) @RequestParam String episodeId,
      @Parameter(description = "개설 과목 코드", required = true) @RequestParam String offeredSubjectsId,
      @RequestBody VideoPlayerRunningTimeDTO runningTimeDTO) {
    Optional<UserOwnSubjectVideo> userOwnSubjectVideo = videoPlayerUserOwnSubjectVideoRepository
        .findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsId(sessionId, episodeId, offeredSubjectsId);
    Optional<VideoPlayerSubjectOwnVideo> videoPlayerSubjectOwnVideo = videoPlayerSubjectOwnVideoRepository
        .findBySovOfferedSubjectsIdAndEpisodeId(userOwnSubjectVideo.get().getUosvOfferedSubjectsId(),
            userOwnSubjectVideo.get().getUosvEpisodeId());
    Optional<Video> videoPlayerVideo = videoPlayerVideoRepository
        .findByVideoId(videoPlayerSubjectOwnVideo.get().getSovVideoId());
    // 잘못된 사용자이면 0초를 반환하고 이후에 프론트에서는 이게 final 값이니까 영상 실행이 안됨
    if (userOwnSubjectVideo.isEmpty())
      return 0;

    // 특정 과목의 영상에서 영상의 최대 위치보다 유저가 현재 시청하고 있는 시점이 미치지 못한 경우 -> 업데이트
    // 기존에 시청했던 위치보다 더 앞의 위치를 시청하는 경우 -> 업데이트
    if (Integer.parseInt(videoPlayerVideo.get().getMax()) > Integer.parseInt(userOwnSubjectVideo.get().getUosvFinal())
        && Integer.parseInt(userOwnSubjectVideo.get().getUosvFinal()) < Integer
            .parseInt(runningTimeDTO.getFinalLocation())) {
      userOwnSubjectVideo.get().setUosvFinal(runningTimeDTO.getFinalLocation());
      // 해당 과목에 대한 영상 진행도 업데이트
      userOwnSubjectVideo.get()
          .setProgress(Integer.toString((int) Math.ceil(Integer.parseInt(runningTimeDTO.getFinalLocation())
              / (double) Integer.parseInt(videoPlayerVideo.get().getMax()) * 100)));
      videoPlayerUserOwnSubjectVideoRepository.save(userOwnSubjectVideo.get());
    }
    return Integer.parseInt(userOwnSubjectVideo.get().getUosvFinal()); // 갱신된 final 값
  }
}