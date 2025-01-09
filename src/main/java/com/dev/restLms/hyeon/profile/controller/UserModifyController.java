package com.dev.restLms.hyeon.profile.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.User;
import com.dev.restLms.hyeon.profile.DTO.UserCredentialsDTO;
import com.dev.restLms.hyeon.profile.DTO.UserProfileUpdateDTO;
import com.dev.restLms.hyeon.profile.projection.ProfileUser;
import com.dev.restLms.hyeon.profile.repository.ProfileFileinfoRepository;
import com.dev.restLms.hyeon.profile.repository.ProfileUserRepository;
import com.dev.restLms.hyeon.profile.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "나의 정보 조회 API", description = "나의 정보 조회 API")
public class UserModifyController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileUserRepository profileUserRepository;

    @Autowired
    ProfileFileinfoRepository fileinfoRepository;

    @GetMapping("/userProfile")
    @Operation(summary = "나의 정보 조회", description = "주어진 SESSION_ID로 사용자의 정보를 조회합니다.")
    public ResponseEntity<?> getUserProfileInfo() {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            final String userSessionId = auth.getPrincipal().toString();

            Optional<ProfileUser> profileUser = profileUserRepository.findBySessionId(userSessionId);
            if (profileUser.isEmpty()) {
                return ResponseEntity.status(404).body("사용자 정보를 찾을 수 없습니다.");
            }

            ProfileUser user = profileUser.get();
            Map<String, String> userInfo = new HashMap<>();
            String formattedDate = LocalDateTime.now().plusDays(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String pcd = "";
            try {
                pcd = getDaysSincePwChange(user.getPwChangeDate(), formattedDate);
            } catch (Exception e) {
                pcd = "";
            }

            userInfo.put("sessionId", user.getSessionId());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("userName", user.getUserName());
            userInfo.put("userEmail", user.getUserEmail());
            userInfo.put("phoneNumber", formatPhoneNumber(user.getPhoneNumber()));
            userInfo.put("userBirth", formatBirthDate(user.getUserBirth()));
            userInfo.put("pcd", pcd);
            if (user.getFileNo() == null || user.getFileNo().isEmpty() || user.getFileNo().equals("0")) {
                userInfo.put("fileNo", "16235caa-b7c2-4fb4-98f0-d16cc18c2315");
            } else {
                userInfo.put("fileNo", user.getFileNo());
            }

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/profileUpdate")
    @Operation(summary = "나의 정보 수정", description = "사용자의 정보를 수정합니다.")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileUpdateDTO userProfileUpdateDTO) {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            final String userSessionId = auth.getPrincipal().toString();
            Optional<User> userOptional = userRepository.findBySessionId(userSessionId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("사용자 정보를 찾을 수 없습니다.");
            }
            User user = userOptional.get();
            String phoneNumber = userProfileUpdateDTO.getPhoneNumber();
            String userBirth = userProfileUpdateDTO.getUserBirth();
            if (!phoneNumber.matches("\\d{11}")) {
                return ResponseEntity.status(400).body("전화번호는 11자리 숫자만 입력해주세요. 예: 01012345678.");
            }
            if (!userBirth.matches("\\d{8}")) {
                return ResponseEntity.status(400).body("생년월일은 8자리 숫자만 입력해주세요. 예: 19900529.");
            }
            if (!userProfileUpdateDTO.getUserEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ResponseEntity.status(400).body("유효한 이메일 주소를 입력해주세요. 예: example@example.com.");
            }

            // 이미 같은 내용으로 업데이트된 경우 409 상태 코드 반환
            if (user.getNickname().equals(userProfileUpdateDTO.getNickname())
                    && user.getUserEmail().equals(userProfileUpdateDTO.getUserEmail())
                    && user.getPhoneNumber().equals(phoneNumber) && user.getUserBirth().equals(userBirth)) {
                return ResponseEntity.status(409).body("이미 프로필이 업데이트 되었습니다");
            }

            user.setNickname(userProfileUpdateDTO.getNickname());
            user.setUserEmail(userProfileUpdateDTO.getUserEmail());
            user.setPhoneNumber(formatPhoneNumber(phoneNumber));
            user.setUserBirth(formatBirthDate(userBirth));
            userRepository.save(user);
            return ResponseEntity.ok("사용자 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "Profile/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    @PostMapping("/imageUpdate")
    @Operation(summary = "이미지 수정", description = "사용자의 이미지를 수정합니다.")
    public ResponseEntity<?> updateUserImage(@RequestPart("file") MultipartFile file) {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            final String userSessionId = auth.getPrincipal().toString();
            Optional<User> userOptional = userRepository.findBySessionId(userSessionId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("사용자 정보를 찾을 수 없습니다.");
            }

            User user = userOptional.get();

            if (file != null && !"no-file".equals(file.getOriginalFilename())) { // 파일 크기 확인
                Optional<FileInfo> findFileInfo = fileinfoRepository
                        .findByFileNo(userOptional.get().getFileNo());

                if (findFileInfo.isPresent()) {
                    FileInfo fileInfo = findFileInfo.get();
                    Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 삭제 실패: " + e.getMessage());
                    }
                }

                if (file != null) {
                    // 파일 크기 확인
                    if (file.getSize() > MAX_FILE_SIZE) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                    }
                    // 파일 정보 저장
                    Map<String, Object> result = saveFile(file);
                    Path path = (Path) result.get("path");
                    String uniqueFileName = (String) result.get("uniqueFileName");
                    // 파일의 마지막 경로 (파일명 + 확장자 전까지 저장)
                    String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\") + 1);

                    if (!findFileInfo.isPresent()) {
                        // 고유한 파일 번호 생성
                        String fileNo = UUID.randomUUID().toString();

                        FileInfo fileInfo = FileInfo.builder()
                                .fileNo(fileNo)
                                .fileSize(Long.toString(file.getSize()))
                                .filePath(filePath)
                                .orgFileNm(file.getOriginalFilename())
                                .encFileNm(uniqueFileName)
                                .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                                .uploaderSessionId(userSessionId)
                                .build();
                        fileinfoRepository.save(fileInfo);
                        user.setFileNo(fileNo);
                        userRepository.save(user);
                    } else if (findFileInfo.isPresent()) {
                        FileInfo fileInfo = findFileInfo.get();
                        fileInfo.setFilePath(filePath);
                        fileInfo.setFileSize(Long.toString(file.getSize()));
                        fileInfo.setUploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                        fileInfo.setEncFileNm(uniqueFileName);
                        fileinfoRepository.save(fileInfo);
                        user.setFileNo(user.getFileNo());
                        userRepository.save(user);
                    }
                }
            } else {
                user.setFileNo(user.getFileNo());
            }
            return ResponseEntity.ok("사용자 이미지가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/verifyPassword")
    @Operation(summary = "현재 비밀번호 확인", description = "사용자의 현재 비밀번호를 확인합니다.")
    public ResponseEntity<?> verifyCurrentPassword(@RequestBody UserCredentialsDTO updateDTO) {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            final String userSessionId = auth.getPrincipal().toString();
            Optional<User> userOptional = userRepository.findBySessionId(userSessionId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("사용자 정보를 찾을 수 없습니다.");
            }
            User user = userOptional.get();

            // 현재 비밀번호 확인
            if (user.getUserPw() != null && !user.getUserPw().equals(updateDTO.getUserPw())) {
                return ResponseEntity.status(400).body("잘못된 비밀번호입니다.");
            }

            return ResponseEntity.ok("비밀번호가 일치합니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/passwordUpdate")
    @Operation(summary = "비밀번호 수정", description = "사용자의 비밀번호를 수정합니다.")
    public ResponseEntity<?> updateUserPassword(@RequestBody UserCredentialsDTO updateDTO) {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            final String userSessionId = auth.getPrincipal().toString();
            Optional<User> userOptional = userRepository.findBySessionId(userSessionId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("사용자 정보를 찾을 수 없습니다.");
            }

            User user = userOptional.get();
            if (user.getUserPw() != null && user.getUserPw().equals(updateDTO.getUserPw())) {
                return ResponseEntity.status(400).body("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
            } else if (user.getUserPw() == null && !user.getUserPw().matches(".{8,}")) {
                return ResponseEntity.status(400).body("8자리 이상 입력해주세요");
            }

            user.setUserPw(updateDTO.getUserPw());// 비밀번호 변경
            user.setPwChangeDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            userRepository.save(user);
            return ResponseEntity.ok("사용자 비밀번호가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    };

    private Map<String, Object> saveFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + uniqueFileName);
        Files.createDirectories(path.getParent());
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);
        return result;
    }

    private String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }

    private String formatBirthDate(String birthDate) {
        return birthDate.replaceAll("(\\d{4})(\\d{2})(\\d{2})", "$1년 $2월 $3일");
    }

    public static String getDaysSincePwChange(String pwChangeDate, String currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime start = LocalDateTime.parse(pwChangeDate, formatter);
        LocalDateTime end = LocalDateTime.parse(currentDate, formatter);

        long durationInDays = ChronoUnit.DAYS.between(start, end);
        return durationInDays + "";
    }
}
