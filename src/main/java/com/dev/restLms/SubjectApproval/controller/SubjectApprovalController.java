package com.dev.restLms.SubjectApproval.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.SubjectApproval.persistence.SubjectApprovalBaordRepository;
import com.dev.restLms.SubjectApproval.persistence.SubjectApprovalCourseOwnSubjectRepository;
import com.dev.restLms.SubjectApproval.persistence.SubjectApprovalFileInfoRepository;
import com.dev.restLms.SubjectApproval.persistence.SubjectApprovalOfferedSubjectsRepository;
import com.dev.restLms.SubjectApproval.persistence.SubjectApprovalSubjectRepository;
import com.dev.restLms.SubjectApproval.persistence.SubjectApprovalUserRepository;
import com.dev.restLms.SubjectApproval.projection.SubjectApprovalCourseOwnSubject;
import com.dev.restLms.SubjectApproval.projection.SubjectApprovalSubject;
import com.dev.restLms.SubjectApproval.projection.SubjectApprovalUser;
import com.dev.restLms.entity.Board;
import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.OfferedSubjects;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/subjectApproval")
@Tag(name = "SubjectApprovalController", description = "과목 승인 페이지")
public class SubjectApprovalController {

    @Autowired
    private SubjectApprovalSubjectRepository subjectApprovalSubjectRepository;

    @Autowired
    private SubjectApprovalCourseOwnSubjectRepository subjectApprovalCourseOwnSubjectRepository;

    @Autowired
    private SubjectApprovalUserRepository subjectApprovalUserRepository;

    @Autowired
    private SubjectApprovalOfferedSubjectsRepository subjectApprovalOfferedSubjectsRepository;

    @Autowired
    private SubjectApprovalFileInfoRepository subjectApprovalFileInfoRepository;

    @Autowired
    private SubjectApprovalBaordRepository subjectApprovalBaordRepository;

    @PostMapping("/serachSubject")
    public ResponseEntity<?> subjectApproval(
        @RequestParam String subjectName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "7") int size
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                    .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                List<Map<String, Object>> resultList = new ArrayList<>();

                List<SubjectApprovalSubject> findSubjects = subjectApprovalSubjectRepository.findBySubjectNameContaining(subjectName, Sort.by(Sort.Direction.ASC, "subjectName"));

                for(SubjectApprovalSubject findSubject : findSubjects ){

                    Optional<SubjectApprovalCourseOwnSubject> subjectApprovalCheck = subjectApprovalCourseOwnSubjectRepository.findBySubjectIdAndSubjectApprovalAndOfficerSessionId(findSubject.getSubjectId(), "F", sessionId);

                    if(subjectApprovalCheck.isPresent()){

                        Optional<SubjectApprovalUser> findTeacherName = subjectApprovalUserRepository.findBySessionId(findSubject.getTeacherSessionId());

                        if(findTeacherName.isPresent()){

                            Map<String, Object> subjectMap = new HashMap<>();
                            subjectMap.put("subjectId", findSubject.getSubjectId());
                            subjectMap.put("increaseId", subjectApprovalCheck.get().getIncreaseId());
                            subjectMap.put("subjectName", findSubject.getSubjectName());
                            subjectMap.put("teacherName", findTeacherName.get().getUserName());
                            subjectMap.put("teacherSessionId", findSubject.getTeacherSessionId());

                            resultList.add(subjectMap);

                        }

                    }

                }

                // 페이징 처리
                int totalItems = resultList.size();
                int start = Math.min(page * size, totalItems);
                int end = Math.min((page + 1) * size, totalItems);
                List<Map<String, Object>> pagedResult = resultList.subList(start, end);

                // 전체 페이지 수 계산
                int totalPages = (int) Math.ceil((double) totalItems / size);

                // 결과를 포함한 Map 생성
                Map<String, Object> response = new HashMap<>();
                response.put("currentPage", page);
                response.put("totalItems", totalItems);
                response.put("totalPages", totalPages);
                response.put("content", pagedResult);

                return ResponseEntity.ok().body(response);
                
            } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

    }

    @GetMapping("/approval")
    public ResponseEntity<?> finallyApproval(
        @RequestParam String increaseId,
        @RequestParam String subjectId
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                    .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                Optional<CourseOwnSubject> findSubject = subjectApprovalCourseOwnSubjectRepository.findByIncreaseIdAndSubjectIdAndOfficerSessionId(increaseId, subjectId, sessionId);

                if(findSubject.isPresent()){

                    Optional<SubjectApprovalSubject> findTeacherSessionId = subjectApprovalSubjectRepository.findBySubjectId(subjectId);

                    if(findTeacherSessionId.isPresent()){

                        CourseOwnSubject courseOwnSubject = findSubject.get();
                        courseOwnSubject.setSubjectApproval("T");
    
                        subjectApprovalCourseOwnSubjectRepository.save(courseOwnSubject);
    
                        OfferedSubjects offeredSubjects = OfferedSubjects.builder()
                        .officerSessionId(sessionId)
                        .teacherSessionId(findTeacherSessionId.get().getTeacherSessionId())
                        .subjectId(subjectId)
                        .courseId("individual-subjects")
                        .build();

                        subjectApprovalOfferedSubjectsRepository.save(offeredSubjects);

                        Board board = Board.builder()
                        .boardCategory(findTeacherSessionId.get().getSubjectName() +" Q&A 게시판")
                        .teacherSessionId(findTeacherSessionId.get().getTeacherSessionId())
                        .offeredSubjectsId(offeredSubjects.getOfferedSubjectsId())
                        .build();

                        subjectApprovalBaordRepository.save(board);

                        return ResponseEntity.ok().body(findTeacherSessionId.get().getSubjectName()+" 과목 승인 완료");

                    }


                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body("과목을 찾을 수 없습니다.");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

    }

    @GetMapping("/discard")
    public ResponseEntity<?> subjectWithdrawal(
        @RequestParam String subjectId,
        @RequestParam String increaseId
        ) {

            try {
                
                Optional<CourseOwnSubject> courseOwnSubjectCheck = subjectApprovalCourseOwnSubjectRepository.findByIncreaseId(increaseId);
    
                if(courseOwnSubjectCheck.isPresent()){
    
                    Optional<SubjectApprovalSubject> subjectCheck = subjectApprovalSubjectRepository.findBySubjectId(subjectId);
    
                    if(subjectCheck.isPresent()){
    
                        subjectApprovalCourseOwnSubjectRepository.deleteById(increaseId);
    
                        subjectApprovalSubjectRepository.deleteById(subjectId);
    
                        return ResponseEntity.ok().body(subjectCheck.get().getSubjectName()+" 과목 철회 완료");
    
                    }
    
                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body("과목을 찾을 수 없습니다.");

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

    }

    @GetMapping("/details")
    public ResponseEntity<?> subjectDetails(
        @RequestParam String subjectId
        ) {

            try {

                Optional<SubjectApprovalSubject> findSubject = subjectApprovalSubjectRepository.findBySubjectId(subjectId);

                if(findSubject.isPresent()){

                    Map<String, String> subjectMap = new HashMap<>();
                    subjectMap.put("subjectName", findSubject.get().getSubjectName());
                    subjectMap.put("subjectDesc", findSubject.get().getSubjectDesc());
                    subjectMap.put("subjectCategory", findSubject.get().getSubjectCategory());
                    subjectMap.put("subjectPromotion", findSubject.get().getSubjectPromotion());
                    subjectMap.put("subjectImageLink", "026a765e-1eea-4a84-b010-3f51e825c60a");

                    return ResponseEntity.ok().body(subjectMap);

                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body("과목에 대한 정보가 없습니다.");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

    }

    // 이미지 반환 
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = subjectApprovalFileInfoRepository.findByFileNo(fileNo);
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
    
    
    
    
    
}
