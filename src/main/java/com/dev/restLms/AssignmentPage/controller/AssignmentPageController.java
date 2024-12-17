package com.dev.restLms.AssignmentPage.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.AssignmentPage.projection.AssignmentPageUserOwnAssignmentEvaluationProjection;
import com.dev.restLms.AssignmentPage.projection.AssignmentPagesubjectNameProjection;
import com.dev.restLms.AssignmentPage.dto.UserOwnAssignmentEvaluationDTO;
import com.dev.restLms.AssignmentPage.projection.AssignmentPageAssignmentProjection;
import com.dev.restLms.AssignmentPage.projection.AssignmentPageOfferedSubjectsProjection;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageAssignmentRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageFileInfoRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageOfferedSubjectsRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageSubjectRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageUserOwnAssignmentEvaluationRepository;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.UserOwnAssignmentEvaluation;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;


@RestController
@Tag(name = "Assignmnet API", description = "과제에 대한 API 목록")
@RequestMapping("/Assignment")
public class AssignmentPageController {
  @Autowired
  private AssignmentPageAssignmentRepository assignmentRepo;

  @Autowired
  private AssignmentPageUserOwnAssignmentEvaluationRepository userOwnAssignmentEvaluationRepo;

  @Autowired
  private AssignmentPageOfferedSubjectsRepository offeredSubjectsRepository;

  @Autowired
  private AssignmentPageSubjectRepository subjectRepo;

  @Autowired
  private AssignmentPageFileInfoRepository filePepo;

  private static final String ROOT_DIR = "WeGoHiveFile/";
  private static final String UPLOAD_DIR = "Subject/";
  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)
    
    @GetMapping("/getSpecificUserAssignmentTotal")
    public ResponseEntity<?> getSpecificUserAssignmentTotal(
        @RequestParam(value = "sessionId", required = true) String sessionId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> resultList = new ArrayList<>();

        Page<AssignmentPageUserOwnAssignmentEvaluationProjection> pagedUserAssignments =
            userOwnAssignmentEvaluationRepo.findByUoaeSessionId(sessionId, pageable);

        if (pagedUserAssignments.hasContent()) {
            for (AssignmentPageUserOwnAssignmentEvaluationProjection userOwnAssignmentEvaluation : pagedUserAssignments.getContent()) {
                Page<AssignmentPageAssignmentProjection> assignments =
                    assignmentRepo.findByAssignmentIdAndTeacherSessionId(
                        userOwnAssignmentEvaluation.getAssignmentId(),
                        userOwnAssignmentEvaluation.getTeacherSessionId(),
                        pageable
                    );

                if (assignments.hasContent()) {
                    for (AssignmentPageAssignmentProjection assignment : assignments.getContent()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("fileNo", userOwnAssignmentEvaluation.getFileNo());
                        resultMap.put("isSubmit", userOwnAssignmentEvaluation.getIsSubmit());
                        resultMap.put("score", userOwnAssignmentEvaluation.getScore());
                        resultMap.put("assignmnetId", userOwnAssignmentEvaluation.getAssignmentId());
                        resultMap.put("assignmentTitle", assignment.getAssignmentTitle());
                        resultMap.put("assignmentContent", assignment.getAssignmentContent());
                        resultMap.put("cutLine", assignment.getCutline());
                        resultMap.put("deadLine", assignment.getDeadline());

                        Optional<AssignmentPageOfferedSubjectsProjection> offeredSubjectData =
                            offeredSubjectsRepository.findByOfferedSubjectsId(assignment.getOfferedSubjectsId());

                        if (offeredSubjectData.isPresent()) {
                            AssignmentPageOfferedSubjectsProjection subjectId = offeredSubjectData.get();
                            Optional<AssignmentPagesubjectNameProjection> subjectData =
                                subjectRepo.findBySubjectId(subjectId.getSubjectId());

                            if (subjectData.isPresent()) {
                                AssignmentPagesubjectNameProjection subjectName = subjectData.get();
                                resultMap.put("subjectName", subjectName.getSubjectName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultList);
            response.put("currentPage", pagedUserAssignments.getNumber());
            response.put("totalPages", pagedUserAssignments.getTotalPages());
            response.put("totalItems", pagedUserAssignments.getTotalElements());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    @GetMapping("/getSpecificUserAssignmentComplete")
    public ResponseEntity<?> getSpecificUserAssignmentComplete(
        @RequestParam(value = "sessionId", required = true) String sessionId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> resultList = new ArrayList<>();

        // "isSubmit"이 "t"인 데이터만 가져오기
        Page<AssignmentPageUserOwnAssignmentEvaluationProjection> pagedUserAssignments =
            userOwnAssignmentEvaluationRepo.findByUoaeSessionIdAndIsSubmit(sessionId, "t", pageable);

        if (pagedUserAssignments.hasContent()) {
            for (AssignmentPageUserOwnAssignmentEvaluationProjection userOwnAssignmentEvaluation : pagedUserAssignments.getContent()) {
                Page<AssignmentPageAssignmentProjection> assignments =
                    assignmentRepo.findByAssignmentIdAndTeacherSessionId(
                        userOwnAssignmentEvaluation.getAssignmentId(),
                        userOwnAssignmentEvaluation.getTeacherSessionId(),
                        pageable
                    );

                if (assignments.hasContent()) {
                    for (AssignmentPageAssignmentProjection assignment : assignments.getContent()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("fileNo", userOwnAssignmentEvaluation.getFileNo());
                        resultMap.put("isSubmit", userOwnAssignmentEvaluation.getIsSubmit());
                        resultMap.put("score", userOwnAssignmentEvaluation.getScore());
                        resultMap.put("assignmnetId", userOwnAssignmentEvaluation.getAssignmentId());
                        resultMap.put("assignmentTitle", assignment.getAssignmentTitle());
                        resultMap.put("assignmentContent", assignment.getAssignmentContent());
                        resultMap.put("cutLine", assignment.getCutline());
                        resultMap.put("deadLine", assignment.getDeadline());

                        Optional<AssignmentPageOfferedSubjectsProjection> offeredSubjectData =
                            offeredSubjectsRepository.findByOfferedSubjectsId(assignment.getOfferedSubjectsId());

                        if (offeredSubjectData.isPresent()) {
                            AssignmentPageOfferedSubjectsProjection subjectId = offeredSubjectData.get();
                            Optional<AssignmentPagesubjectNameProjection> subjectData =
                                subjectRepo.findBySubjectId(subjectId.getSubjectId());

                            if (subjectData.isPresent()) {
                                AssignmentPagesubjectNameProjection subjectName = subjectData.get();
                                resultMap.put("subjectName", subjectName.getSubjectName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultList);
            response.put("currentPage", pagedUserAssignments.getNumber());
            response.put("totalPages", pagedUserAssignments.getTotalPages());
            response.put("totalItems", pagedUserAssignments.getTotalElements());

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/getSpecificUserAssignmentIncomplete")
    public ResponseEntity<?> getSpecificUserAssignmentIncomplete(
        @RequestParam(value = "sessionId", required = true) String sessionId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> resultList = new ArrayList<>();

        // "isSubmit"이 "t"인 데이터만 가져오기
        Page<AssignmentPageUserOwnAssignmentEvaluationProjection> pagedUserAssignments =
            userOwnAssignmentEvaluationRepo.findByUoaeSessionIdAndIsSubmit(sessionId, "f", pageable);

        if (pagedUserAssignments.hasContent()) {
            for (AssignmentPageUserOwnAssignmentEvaluationProjection userOwnAssignmentEvaluation : pagedUserAssignments.getContent()) {
                Page<AssignmentPageAssignmentProjection> assignments =
                    assignmentRepo.findByAssignmentIdAndTeacherSessionId(
                        userOwnAssignmentEvaluation.getAssignmentId(),
                        userOwnAssignmentEvaluation.getTeacherSessionId(),
                        pageable
                    );

                if (assignments.hasContent()) {
                    for (AssignmentPageAssignmentProjection assignment : assignments.getContent()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("fileNo", userOwnAssignmentEvaluation.getFileNo());
                        resultMap.put("isSubmit", userOwnAssignmentEvaluation.getIsSubmit());
                        resultMap.put("score", userOwnAssignmentEvaluation.getScore());
                        resultMap.put("assignmnetId", userOwnAssignmentEvaluation.getAssignmentId());
                        resultMap.put("assignmentTitle", assignment.getAssignmentTitle());
                        resultMap.put("assignmentContent", assignment.getAssignmentContent());
                        resultMap.put("cutLine", assignment.getCutline());
                        resultMap.put("deadLine", assignment.getDeadline());

                        Optional<AssignmentPageOfferedSubjectsProjection> offeredSubjectData =
                            offeredSubjectsRepository.findByOfferedSubjectsId(assignment.getOfferedSubjectsId());

                        if (offeredSubjectData.isPresent()) {
                            AssignmentPageOfferedSubjectsProjection subjectId = offeredSubjectData.get();
                            Optional<AssignmentPagesubjectNameProjection> subjectData =
                                subjectRepo.findBySubjectId(subjectId.getSubjectId());

                            if (subjectData.isPresent()) {
                                AssignmentPagesubjectNameProjection subjectName = subjectData.get();
                                resultMap.put("subjectName", subjectName.getSubjectName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultList);
            response.put("currentPage", pagedUserAssignments.getNumber());
            response.put("totalPages", pagedUserAssignments.getTotalPages());
            response.put("totalItems", pagedUserAssignments.getTotalElements());

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/submitAssignment")
    public ResponseEntity<?> uploadFile(
        @RequestPart("dto") UserOwnAssignmentEvaluationDTO dto,
        @RequestPart("file") MultipartFile file) {
        try {
            // 파일 비어있는지 확인
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
            }
    
            // 파일 크기 확인 (10MB 초과)
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일 크기가 10MB를 초과합니다.");
            }
    
            // 과제 세션 ID와 Assignment ID를 확인
            Optional<UserOwnAssignmentEvaluation> opUserOwnAssignmentEvaluation =
                userOwnAssignmentEvaluationRepo.UoaeSessionIdAndAssignmentId(dto.getUoaeSessionId(), dto.getAssignmentId());
                // System.out.println(dto.getUoaeSessionId());
                // System.out.println(dto.getAssignmentId());
    
            if (opUserOwnAssignmentEvaluation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("과제를 찾을 수 없습니다.");
            }
    
            UserOwnAssignmentEvaluation userOwnAssignmentEvaluation = opUserOwnAssignmentEvaluation.get();
    
            // 이미 제출된 파일이 존재하는 경우 처리
            if (userOwnAssignmentEvaluation.getFileNo() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 제출된 파일이 존재합니다.");
            }

            // 파일 저장
            Map<String, Object> result = saveFile(file, dto);
            Path path = (Path) result.get("path");
            String uniqueFileName = (String) result.get("uniqueFileName");
            // System.out.println(path.toString());
            // System.out.println(uniqueFileName);


            // 파일의 마지막 경로(파일명 + 확장자 전까지를 저장)
            String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\") + 1);
            // 고유한 파일 번호 생성
            String fileNo = UUID.randomUUID().toString();
            FileInfo fileInfo = FileInfo.builder()
                .fileNo(fileNo)
                .fileSize(Long.toString(file.getSize()))
                .filePath(filePath)
                .orgFileNm(file.getOriginalFilename())
                .encFileNm(uniqueFileName)
                .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .uploaderSessionId(dto.getUoaeSessionId())
                .build();
            // 파일 정보 저장
            filePepo.save(fileInfo);
    
            // 과제 상태 업데이트
            userOwnAssignmentEvaluation.setFileNo(fileNo);
            userOwnAssignmentEvaluation.setIsSubmit("t");
            userOwnAssignmentEvaluationRepo.save(userOwnAssignmentEvaluation);
    
            return ResponseEntity.ok().body("파일 업로드 성공: " + file.getOriginalFilename());
    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
        }
    }
    
    // 파일 저장 메서드
    private Map<String, Object> saveFile(MultipartFile file, UserOwnAssignmentEvaluationDTO dto) throws Exception {
        // 원본 파일명에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String fileExtension = ""; // . 부터 시작하는 확장자를 담는 변수

        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
        }

        // 고유한 파일명 생성 (UUID + 확장자)
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로 : root 디렉터리( WeGoHiveFile / Subject/ "과목 명" / "과제 명" )
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + dto.getSubjectName() + '/' + dto.getAssignmentName() + '/' + uniqueFileName);

        // 경로가 존재하지 않으면 생성
        Files.createDirectories(path.getParent());

        // 파일 저장
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);
        return result;
    }
}