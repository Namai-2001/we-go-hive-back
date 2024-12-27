package com.dev.restLms.sechan.teacherVideo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.SubjectOwnVideo;
import com.dev.restLms.entity.Video;
import com.dev.restLms.sechan.teacherVideo.repository.TV_OS_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_SOV_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_S_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_V_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/teacher/video-management")
@Tag(name = "Video Management", description = "강사의 영상 관리 기능")
public class TV_Controller {

    @Autowired
    private TV_S_Repository tv_s_repository;

    @Autowired
    private TV_OS_Repository tv_os_repository;

    @Autowired
    private TV_SOV_Repository tv_sov_repository;

    @Autowired
    private TV_V_Repository tv_v_repository;

    @GetMapping("/dashboard")
    @Operation(summary = "강사가 개설한 과목과 영상 목록 조회", 
               description = "강사가 개설한 모든 과목과 각 과목에 연결된 영상 목록을 반환")
    public ResponseEntity<?> getSubjectsAndVideos(@RequestParam String teacherSessionId) {
        // 1. 강사가 개설한 과목 가져오기
        List<OfferedSubjects> offeredSubjects = tv_os_repository.findByTeacherSessionId(teacherSessionId);

        // 2. 각 과목별로 연결된 영상 목록 구성
        List<Map<String, Object>> subjectsWithVideos = new ArrayList<>();
        for (OfferedSubjects os : offeredSubjects) {
            Map<String, Object> subjectInfo = new HashMap<>();

            // 과목명 가져오기
            Optional<Subject> subject = tv_s_repository.findById(os.getSubjectId());
            String subjectName = "과목 이름 없음";
            if (subject.isPresent()) {
                subjectName = subject.get().getSubjectName();
            }

            // 과목 정보 저장
            subjectInfo.put("offeredSubjectsId", os.getOfferedSubjectsId());
            subjectInfo.put("subjectName", subjectName);

            // 과목에 연결된 영상 목록 조회
            List<SubjectOwnVideo> subjectOwnVideos = tv_sov_repository.findBySovOfferedSubjectsId(os.getOfferedSubjectsId());
            List<String> videoIds = new ArrayList<>();
            for (SubjectOwnVideo sov : subjectOwnVideos) {
                videoIds.add(sov.getSovVideoId());
            }

            List<Video> videos = tv_v_repository.findByVideoIdIn(videoIds);

            // 영상 정보 구성
            List<Map<String, Object>> videoList = new ArrayList<>();
            for (SubjectOwnVideo sov : subjectOwnVideos) {
                Map<String, Object> videoInfo = new HashMap<>();
                Optional<Video> video = videos.stream().filter(v -> v.getVideoId().equals(sov.getSovVideoId())).findFirst();

                videoInfo.put("videoId", sov.getSovVideoId());
                videoInfo.put("videoTitle", video.map(Video::getVideoTitle).orElse("제목 없음"));
                videoInfo.put("videoLink", video.map(Video::getVideoLink).orElse("링크 없음"));
                videoInfo.put("videoImg", video.map(Video::getVideoImg).orElse("이미지 없음"));
                videoInfo.put("max", video.map(Video::getMax).orElse("0"));
                videoInfo.put("videoSortIndex", sov.getVideoSortIndex());

                videoList.add(videoInfo);
            }

            // 정렬 수행
            videoList.sort(Comparator.comparing(v -> Integer.parseInt((String) v.get("videoSortIndex"))));

            // 과목 정보와 영상 목록 저장
            subjectInfo.put("videos", videoList);
            subjectsWithVideos.add(subjectInfo);
        }

        // 3. 데이터 반환
        return ResponseEntity.ok(subjectsWithVideos);
    }
}
