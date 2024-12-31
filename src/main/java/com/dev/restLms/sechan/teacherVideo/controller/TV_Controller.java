package com.dev.restLms.sechan.teacherVideo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.SubjectOwnVideo;
import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.entity.Video;
import com.dev.restLms.sechan.teacherVideo.repository.TV_C_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_File_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_OS_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_SOV2_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_SOV3_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_SOV_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_S_Repository;
import com.dev.restLms.sechan.teacherVideo.repository.TV_UOSV_Repository;
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

    @Autowired
    private TV_File_Repository fileRepo;

    @Autowired
    private TV_SOV2_Repository tv_sov2_repository;

    @Autowired
    private TV_SOV3_Repository tv_sov3_repository;

    @Autowired
    private TV_C_Repository tv_c_repository;

    @Autowired
    private TV_UOSV_Repository tv_uosv_repository;

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "SubjectVideo/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    public static String extractYoutubeId(String url) {
        String regex = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/.*v=|youtu\\.be\\/)([a-zA-Z0-9_-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1); // 유튜브 ID 추출
        }
        throw new IllegalArgumentException("Invalid YouTube URL");
    }

    public static int getYoutubeVideoDuration(String youtubeId) throws IOException {
        String url = "https://www.youtube.com/watch?v=" + youtubeId;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 5초 타임아웃
        connection.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 메타 태그에서 "duration" 속성 검색
                if (line.contains("\"duration\"")) {
                    String duration = extractDurationFromMetaTag(line);
                    return convertIso8601DurationToSeconds(duration); // ISO8601 형식 -> 초 단위로 변환
                }
            }
        }

        throw new IOException("Failed to fetch video duration");
    }

    // 메타 태그에서 ISO8601 지속 시간 추출
    private static String extractDurationFromMetaTag(String line) {
        String regex = "content=\"(PT[^\"]+)\""; // 예: content="PT2M15S"
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid duration format in meta tag");
    }

    // ISO8601 형식("PT2M15S")의 지속 시간을 초로 변환
    public static int convertIso8601DurationToSeconds(String duration) {
        int hours = 0, minutes = 0, seconds = 0;

        // 정규식 매칭
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(duration);

        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                hours = Integer.parseInt(matcher.group(1));
            }
            if (matcher.group(2) != null) {
                minutes = Integer.parseInt(matcher.group(2));
            }
            if (matcher.group(3) != null) {
                seconds = Integer.parseInt(matcher.group(3));
            }
        }

        // 전체 시간을 초 단위로 반환
        return hours * 3600 + minutes * 60 + seconds;
    }

    // 파일 저장 메서드
    private Map<String, Object> saveFile(MultipartFile file, String videoTitle) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 고유 파일명 생성성
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로로
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + videoTitle + "/" + uniqueFileName);

        // 파일이 존재하지 않으면 생성성
        Files.createDirectories(path.getParent());

        // 파일저장
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);

        return result;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "강사가 개설한 과목과 영상 목록 조회", description = "강사가 개설한 모든 과목과 각 과목에 연결된 영상 목록을 반환")
    public ResponseEntity<?> getSubjectsAndVideos() {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션 아이디 보안 컨텍스트에서 가져오기
        String teacherSessionId = auth.getPrincipal().toString();
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

            // 과정명 가져오기
            String courseTitle = "과정 정보 없음";
            if ("individual-subjects".equals(os.getCourseId())) {
                courseTitle = "개별 과목";
            } else {
                Optional<Course> course = tv_c_repository.findById(os.getCourseId());
                if (course.isPresent()) {
                    courseTitle = course.get().getCourseTitle();
                }
            }

            // 과목 정보 저장
            subjectInfo.put("offeredSubjectsId", os.getOfferedSubjectsId());
            subjectInfo.put("subjectName", subjectName);
            subjectInfo.put("courseTitle", courseTitle);

            // 과목에 연결된 영상 목록 조회
            List<SubjectOwnVideo> subjectOwnVideos = tv_sov_repository
                    .findBySovOfferedSubjectsId(os.getOfferedSubjectsId());
            List<String> videoIds = new ArrayList<>();
            for (SubjectOwnVideo sov : subjectOwnVideos) {
                videoIds.add(sov.getSovVideoId());
            }

            List<Video> videos = tv_v_repository.findByVideoIdIn(videoIds);

            // 영상 정보 구성
            List<Map<String, Object>> videoList = new ArrayList<>();
            for (SubjectOwnVideo sov : subjectOwnVideos) {
                Map<String, Object> videoInfo = new HashMap<>();
                Optional<Video> video = videos.stream().filter(v -> v.getVideoId().equals(sov.getSovVideoId()))
                        .findFirst();

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

    @PostMapping(value = "/add-video-to-subject/{offeredSubjectsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "과목에 영상 추가", description = "선택한 과목(offeredSubjectsId)에 새로운 영상을 추가")
    public ResponseEntity<?> addVideoToSubject(
            @PathVariable String offeredSubjectsId,
            @RequestPart("videoData") Map<String, Object> videoData,
            @RequestPart("file") MultipartFile file) {
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("파일 크기가 제한을 초과했습니다.");
            }

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            String teacherSessionId = auth.getPrincipal().toString();

            // 요청 데이터 추출
            String videoLink = (String) videoData.get("videoLink");
            String videoTitle = (String) videoData.get("videoTitle");
            String videoSortIndex = (String) videoData.get("videoSortIndex");

            // 유튜브 ID 추출 및 영상 길이 가져오기
            String youtubeId = extractYoutubeId(videoLink);
            int max = getYoutubeVideoDuration(youtubeId);
            if (max > 3)
                max -= 3;

            // 파일 저장
            Map<String, Object> fileSaveResult = saveFile(file, videoTitle);
            Path path = (Path) fileSaveResult.get("path");
            String uniqueFileName = (String) fileSaveResult.get("uniqueFileName");
            String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\") + 1);
            String fileNo = UUID.randomUUID().toString();
            FileInfo fileInfo = FileInfo.builder()
                    .fileNo(fileNo)
                    .fileSize(Long.toString(file.getSize()))
                    .filePath(filePath)
                    .orgFileNm(file.getOriginalFilename())
                    .encFileNm(uniqueFileName)
                    .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                    .uploaderSessionId(teacherSessionId)
                    .build();

            fileRepo.save(fileInfo);

            // 1. Video 엔티티 저장
            Video video = new Video();
            video.setVideoTitle(videoTitle);
            video.setVideoLink(youtubeId);
            video.setMax(String.valueOf(max));
            video.setVideoImg(fileNo);
            tv_v_repository.save(video);

            // 2. SubjectOwnVideo 엔티티 저장
            SubjectOwnVideo subjectOwnVideo = new SubjectOwnVideo();
            subjectOwnVideo.setSovOfferedSubjectsId(offeredSubjectsId);
            subjectOwnVideo.setSovVideoId(video.getVideoId());
            subjectOwnVideo.setVideoSortIndex(videoSortIndex);
            tv_sov_repository.save(subjectOwnVideo);

            // 3. UserOwnSubjectVideo 업데이트 - 이미 수강 중인 사용자들에게 새로운 영상 추가
            List<UserOwnSubjectVideo> userVideos = tv_uosv_repository.findByUosvOfferedSubjectsId(offeredSubjectsId);

            // 기존 episodeId를 리스트로 수집
            List<String> existingEpisodeIds = new ArrayList<>();
            for (UserOwnSubjectVideo userVideo : userVideos) {
                String episodeId = userVideo.getUosvEpisodeId();
                if (!existingEpisodeIds.contains(episodeId)) {
                    existingEpisodeIds.add(episodeId);
                }
            }

            String newEpisodeId = subjectOwnVideo.getEpisodeId();

            // 새로운 영상이 기존 사용자 데이터에 없는 경우 추가
            if (!existingEpisodeIds.contains(newEpisodeId)) {
                for (UserOwnSubjectVideo userVideo : userVideos) {
                    UserOwnSubjectVideo newUserVideo = new UserOwnSubjectVideo();
                    newUserVideo.setUosvSessionId(userVideo.getUosvSessionId());
                    newUserVideo.setUosvEpisodeId(newEpisodeId);
                    newUserVideo.setUosvOfferedSubjectsId(offeredSubjectsId);
                    newUserVideo.setProgress("0");
                    newUserVideo.setUosvFinal("0");
                    tv_uosv_repository.save(newUserVideo);
                }
            }

            return ResponseEntity.ok("영상과 과목 연결 정보가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("영상 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // 이미지 반환
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = fileRepo.findByFileNo(fileNo);
            if (!fileInfoOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            FileInfo fileInfo = fileInfoOptional.get();
            Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // 이미지 형식에 맞게 설정
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/subject/{offeredSubjectsId}")
    @Operation(summary = "특정 과목의 영상 목록 조회", description = "특정 과목(offeredSubjectsId)의 영상 정보를 반환")
    public ResponseEntity<?> getVideosBySubject(@PathVariable String offeredSubjectsId) {
        try {
            // 인증된 강사의 세션 ID 가져오기
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            String teacherSessionId = auth.getPrincipal().toString();

            // 과목이 해당 강사가 소유한 과목인지 확인
            Optional<OfferedSubjects> offeredSubject = tv_os_repository.findById(offeredSubjectsId);
            if (offeredSubject.isEmpty() || !offeredSubject.get().getTeacherSessionId().equals(teacherSessionId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            // 과목에 연결된 영상 목록 조회
            List<SubjectOwnVideo> subjectOwnVideos = tv_sov_repository.findBySovOfferedSubjectsId(offeredSubjectsId);
            List<String> videoIds = new ArrayList<>();
            for (SubjectOwnVideo sov : subjectOwnVideos) {
                videoIds.add(sov.getSovVideoId());
            }

            // 영상 정보 조회
            List<Video> videos = tv_v_repository.findByVideoIdIn(videoIds);

            // 영상 정보 구성
            List<Map<String, Object>> videoList = new ArrayList<>();
            for (SubjectOwnVideo sov : subjectOwnVideos) {
                Map<String, Object> videoInfo = new HashMap<>();
                Optional<Video> video = videos.stream().filter(v -> v.getVideoId().equals(sov.getSovVideoId()))
                        .findFirst();

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

            return ResponseEntity.ok(videoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }

    @PostMapping(value = "/subject/{offeredSubjectsId}/{videoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "특정 영상 정보 수정", description = "특정 과목(offeredSubjectsId)의 특정 영상(videoId) 정보를 수정")
    public ResponseEntity<?> updateVideoInfo(
            @PathVariable String offeredSubjectsId,
            @PathVariable String videoId,
            @RequestPart("videoData") Map<String, Object> videoData,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // 인증된 강사의 세션 ID 가져오기
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            String teacherSessionId = auth.getPrincipal().toString();

            // 과목이 해당 강사가 소유한 과목인지 확인
            Optional<OfferedSubjects> offeredSubject = tv_os_repository.findById(offeredSubjectsId);
            if (offeredSubject.isEmpty() || !offeredSubject.get().getTeacherSessionId().equals(teacherSessionId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            // 영상이 과목에 속해 있는지 확인
            Optional<SubjectOwnVideo> subjectOwnVideo = tv_sov2_repository
                    .findBySovOfferedSubjectsIdAndSovVideoId(offeredSubjectsId, videoId);
            if (subjectOwnVideo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 영상이 과목에 속해 있지 않습니다.");
            }

            // 영상 정보 수정
            Optional<Video> videoOpt = tv_v_repository.findById(videoId);
            if (videoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 영상 정보를 찾을 수 없습니다.");
            }

            Video video = videoOpt.get();

            // 수정 가능한 필드 업데이트
            if (videoData.containsKey("videoTitle")) {
                video.setVideoTitle(videoData.get("videoTitle").toString());
            }
            if (videoData.containsKey("videoLink")) {
                String newLink = videoData.get("videoLink").toString();
                String youtubeId = extractYoutubeId(newLink);
                video.setVideoLink(youtubeId);

                // 유튜브 영상 길이 가져오기 및 max 값 업데이트
                int max = getYoutubeVideoDuration(youtubeId);
                if (max > 3) {
                    max -= 3; // 3초를 뺌
                }
                video.setMax(String.valueOf(max));
            }
            if (videoData.containsKey("videoSortIndex")) {
                String sortIndex = videoData.get("videoSortIndex").toString();
                subjectOwnVideo.get().setVideoSortIndex(sortIndex);
            }

            // 파일(이미지)이 업로드되었는지 확인 후 처리
            if (file != null && !file.isEmpty()) {
                String videoTitle = video.getVideoTitle();
                Map<String, Object> fileSaveResult = saveFile(file, videoTitle);
                Path path = (Path) fileSaveResult.get("path");
                String uniqueFileName = (String) fileSaveResult.get("uniqueFileName");

                String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\") + 1);
                String fileNo = UUID.randomUUID().toString();

                // FileInfo 저장
                FileInfo fileInfo = FileInfo.builder()
                        .fileNo(fileNo)
                        .fileSize(Long.toString(file.getSize()))
                        .filePath(filePath)
                        .orgFileNm(file.getOriginalFilename())
                        .encFileNm(uniqueFileName)
                        .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                        .uploaderSessionId(teacherSessionId)
                        .build();

                fileRepo.save(fileInfo);

                // 비디오 이미지 필드 업데이트
                video.setVideoImg(fileNo);
            }

            // 저장
            tv_v_repository.save(video);

            return ResponseEntity.ok("영상 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/delete-video/{offeredSubjectsId}/{videoId}")
    @Operation(summary = "영상 삭제", description = "특정 과목(offeredSubjectsId)에서 특정 영상(videoId)을 삭제")
    public ResponseEntity<?> deleteVideoFromSubject(
            @PathVariable String offeredSubjectsId,
            @PathVariable String videoId) {
        try {
            // 인증된 강사의 세션 ID 가져오기
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            String teacherSessionId = auth.getPrincipal().toString();

            // 과목이 해당 강사가 소유한 과목인지 확인
            Optional<OfferedSubjects> offeredSubject = tv_os_repository.findById(offeredSubjectsId);
            if (offeredSubject.isEmpty() || !offeredSubject.get().getTeacherSessionId().equals(teacherSessionId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
            }

            // 영상이 과목에 속해 있는지 확인
            Optional<SubjectOwnVideo> subjectOwnVideo = tv_sov2_repository
                    .findBySovOfferedSubjectsIdAndSovVideoId(offeredSubjectsId, videoId);
            if (subjectOwnVideo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 영상이 과목에 속해 있지 않습니다.");
            }

            // 영상 데이터 삭제
            tv_sov_repository.delete(subjectOwnVideo.get());

            // 영상이 다른 과목에 연결되어 있지 않으면 Video 엔티티도 삭제
            List<SubjectOwnVideo> remainingReferences = tv_sov3_repository.findBySovVideoId(videoId);
            if (remainingReferences.isEmpty()) {
                Optional<Video> video = tv_v_repository.findById(videoId);
                if (video.isPresent()) {
                    // 파일 정보 삭제
                    Optional<FileInfo> fileInfo = fileRepo.findByFileNo(video.get().getVideoImg());
                    if (fileInfo.isPresent()) {
                        Path filePath = Paths.get(fileInfo.get().getFilePath() + fileInfo.get().getEncFileNm());
                        Files.deleteIfExists(filePath); // 파일 삭제
                        fileRepo.delete(fileInfo.get()); // FileInfo 삭제
                    }

                    // Video 엔티티 삭제
                    tv_v_repository.delete(video.get());
                }
            }

            return ResponseEntity.ok("영상이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }
}
