package com.dev.restLms.deleteCourse.controller;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.deleteCourse.persistence.DeleteCourseAssignmentRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseBoardPostRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseBoardRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseBookMarkRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseCommentRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseFileinfoRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseOfferedSubjectsRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseOwnSubjectRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseSubjectOwnVideoRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseSubjectRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnAssignmentEvaluationRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnAssignmentRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnCourseRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnSubjectVideoRepository;
import com.dev.restLms.deleteCourse.persistence.DeleteCourseVideoRepository;
import com.dev.restLms.deleteCourse.projection.DeleteCourse;
import com.dev.restLms.deleteCourse.projection.DeleteCourseAssignment;
import com.dev.restLms.deleteCourse.projection.DeleteCourseBoard;
import com.dev.restLms.deleteCourse.projection.DeleteCourseBoardPost;
import com.dev.restLms.deleteCourse.projection.DeleteCourseBookMark;
import com.dev.restLms.deleteCourse.projection.DeleteCourseComment;
import com.dev.restLms.deleteCourse.projection.DeleteCourseOfferedSubjects;
import com.dev.restLms.deleteCourse.projection.DeleteCourseOwnSubject;
import com.dev.restLms.deleteCourse.projection.DeleteCourseSubjectOwnVideo;
import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnAssignment;
import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnAssignmentEvaluation;
import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnCourse;
import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnSubjectVideo;
import com.dev.restLms.entity.FileInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/deleteCourse")
@Tag(name = "DeleteCourseController", description = "관리자 과정 삭제 페이지")
public class DeleteCourseController {

    @Autowired
    private DeleteCourseRepository deleteCourseRepository;

    @Autowired
    private DeleteCourseFileinfoRepository deleteCourseFileinfoRepository;

    @Autowired
    private DeleteCourseUserOwnCourseRepository deleteCourseUserOwnCourseRepository;

    @Autowired
    private DeleteCourseOwnSubjectRepository deleteCourseOwnSubjectRepository;

    @Autowired
    private DeleteCourseOfferedSubjectsRepository deleteCourseOfferedSubjectsRepository;

    @Autowired
    private DeleteCourseUserOwnSubjectVideoRepository deleteCourseUserOwnSubjectVideoRepository;

    @Autowired
    private DeleteCourseAssignmentRepository deleteCourseAssignmentRepository;

    @Autowired
    private DeleteCourseUserOwnAssignmentRepository deleteCourseUserOwnAssignmentRepository;

    @Autowired
    private DeleteCourseBoardRepository deleteCourseBoardRepository;

    @Autowired
    private DeleteCourseBoardPostRepository deleteCourseBoardPostRepository;

    @Autowired
    private DeleteCourseCommentRepository deleteCourseCommentRepository;

    @Autowired
    private DeleteCourseUserOwnAssignmentEvaluationRepository deleteCourseUserOwnAssignmentEvaluationRepository;

    @Autowired
    private DeleteCourseBookMarkRepository deleteCourseBookMarkRepository;

    @Autowired
    private DeleteCourseSubjectOwnVideoRepository deleteCourseSubjectOwnVideoRepository;

    @Autowired
    private DeleteCourseVideoRepository deleteCourseVideoRepository;

    @Autowired
    private DeleteCourseSubjectRepository deleteCourseSubjectRepository;

    @GetMapping("/Courses")
    @Operation(summary = "책임자의 과정 목록", description = "책임자의 과정 목록을 불러옵니다.")
    public ResponseEntity<?> getCourses(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "7") int size
    ) {

        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
            // 유저 세션아이디 보안 컨텍스트에서 가져오기
            String sessionId = auth.getPrincipal().toString();

            List<Map<String, Object>> resultList = new ArrayList<>();

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "courseStartDate").and(Sort.by(Sort.Direction.ASC, "courseTitle")));
            Page<DeleteCourse> findCourses = deleteCourseRepository.findBySessionId(sessionId, pageable);

            for(DeleteCourse findCourse : findCourses){
                
                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseTitle", findCourse.getCourseTitle());
                courseMap.put("courseStartDate", findCourse.getCourseStartDate());
                courseMap.put("courseId", findCourse.getCourseId());

                resultList.add(courseMap);

            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", resultList);
            response.put("currentPage", findCourses.getNumber());
            response.put("totalItems", findCourses.getTotalElements());
            response.put("totalPages", findCourses.getTotalPages());

            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
        }
    }

    @PostMapping("/searchCourse")
    @Operation(summary = "책임자의 과정 검색", description = "검색한 책임자의 과정 목록을 불러옵니다.")
    public ResponseEntity<?> sreachCourse(
        @RequestParam String courseTitle,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "7") int size
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                List<Map<String, Object>> resultList = new ArrayList<>();

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "courseStartDate").and(Sort.by(Sort.Direction.ASC, "courseTitle")));
                Page<DeleteCourse> findCourses = deleteCourseRepository.findBySessionIdAndCourseTitleContaining(sessionId, courseTitle, pageable);

                for(DeleteCourse findCourse : findCourses){
                    
                    HashMap<String, Object> courseMap = new HashMap<>();
                    courseMap.put("courseTitle", findCourse.getCourseTitle());
                    courseMap.put("courseStartDate", findCourse.getCourseStartDate());
                    courseMap.put("courseId", findCourse.getCourseId());

                    resultList.add(courseMap);

                }

                Map<String, Object> response = new HashMap<>();
                response.put("content", resultList);
                response.put("currentPage", findCourses.getNumber());
                response.put("totalItems", findCourses.getTotalElements());
                response.put("totalPages", findCourses.getTotalPages());

                return ResponseEntity.ok().body(response);
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
            }
    }

    @PostMapping("/deleteCourse")
    @Operation(summary = "책임자의 과정 삭제", description = "과정을 삭제합니다.")
    public ResponseEntity<?> deleteCourse(
        @RequestParam String courseId
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                Optional<DeleteCourse> findCourse = deleteCourseRepository.findByCourseIdAndSessionId(courseId, sessionId);
                
                if(findCourse.isPresent()){
                    
                    // 과정을 삭제할 수 없는 조건
                    Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                    if(Long.parseLong(findCourse.get().getCourseStartDate()) <= nowDate){
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("교육이 시작되어 삭제할 수 없습니다.");
                    }

                    List<DeleteCourseOwnSubject> findSubjectIds = deleteCourseOwnSubjectRepository.findByCourseIdAndOfficerSessionId(courseId, sessionId);

                    for(DeleteCourseOwnSubject findSubjectId : findSubjectIds ){

                        Optional<DeleteCourseOfferedSubjects> findOfferedSubjectId = deleteCourseOfferedSubjectsRepository.findBySubjectIdAndCourseIdAndOfficerSessionId(findSubjectId.getSubjectId(), courseId, sessionId);

                        if(findOfferedSubjectId.isPresent()){

                            // 해당 과목에 북마크가 있는지 확인
                            List<DeleteCourseBookMark> findBookMarkIncreaseIds = deleteCourseBookMarkRepository.findByBmOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                            if(!findBookMarkIncreaseIds.isEmpty()){

                                for(DeleteCourseBookMark findBookMarkIncreaseId : findBookMarkIncreaseIds){

                                    // 북마크 삭제
                                    deleteCourseBookMarkRepository.deleteById(findBookMarkIncreaseId.getIncreaseId());

                                }

                            }

                            // 수강생별 영상 목록 확인 
                            List<DeleteCourseUserOwnSubjectVideo> findUserOwnSubjectVideos = deleteCourseUserOwnSubjectVideoRepository.findByUosvOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                            if(!findUserOwnSubjectVideos.isEmpty()){

                                for(DeleteCourseUserOwnSubjectVideo findUserOwnSubjectVideo : findUserOwnSubjectVideos){
                                    // 사용자별 과목 영상 목록 삭제
                                    deleteCourseUserOwnSubjectVideoRepository.deleteById(findUserOwnSubjectVideo.getIncreaseId());

                                }

                            }

                            // 해당 과목의 영상들 확인 
                            List<DeleteCourseSubjectOwnVideo> findSubjectOwnVideoEpsodeIds = deleteCourseSubjectOwnVideoRepository.findBySovOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                            if(!findSubjectOwnVideoEpsodeIds.isEmpty()){

                                for(DeleteCourseSubjectOwnVideo findSubjectOwnVideoEpsodeId : findSubjectOwnVideoEpsodeIds){
                                    
                                    // 과목별 영상목록에서 영상삭제
                                    deleteCourseSubjectOwnVideoRepository.deleteById(findSubjectOwnVideoEpsodeId.getEpisodeId());

                                    // 영상 삭제
                                    deleteCourseVideoRepository.deleteById(findSubjectOwnVideoEpsodeId.getSovVideoId());

                                }

                            }

                            // ----------------------------------------------------------------------------

                            // 해당 과목의 과제가 있는지 확인 
                            List<DeleteCourseAssignment> findAssignmentIds = deleteCourseAssignmentRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                            if(!findAssignmentIds.isEmpty()){

                                for(DeleteCourseAssignment findAssignmentId : findAssignmentIds){

                                    // 해당 과목의 과제를 한 사용자가 있는지 확인
                                    List<DeleteCourseUserOwnAssignmentEvaluation> findSubmissionIds = deleteCourseUserOwnAssignmentEvaluationRepository.findByAssignmentId(findAssignmentId.getAssignmentId());

                                    if(!findAssignmentIds.isEmpty()){

                                        for(DeleteCourseUserOwnAssignmentEvaluation findSubmissionId : findSubmissionIds){

                                            if(!findSubmissionId.getFileNo().isEmpty()){

                                                Optional<FileInfo> findFilePath = deleteCourseFileinfoRepository.findByFileNo(findSubmissionId.getFileNo());

                                                if(findFilePath.isPresent()){

                                                    FileInfo fileInfo = findFilePath.get();
                                                    Path filePath = Paths.get(fileInfo.getFilePath()+fileInfo.getEncFileNm());

                                                    try {
                                                        Files.deleteIfExists(filePath);
                                                    } catch (Exception e) {
                                                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 삭제 실패: " + e.getMessage());
                                                    }

                                                    deleteCourseFileinfoRepository.deleteById(findSubmissionId.getFileNo());

                                                } 
                                                // 사용자가 한 과제에 파일이 있다면 파일을 삭제 
                                                deleteCourseFileinfoRepository.deleteById(findSubmissionId.getFileNo());

                                            }
                                            // 해당 과목의 사용자 과제 삭제
                                            deleteCourseUserOwnAssignmentEvaluationRepository.deleteById(findAssignmentId.getAssignmentId());

                                        }

                                    }
                                    // 해당 과목의 과제 삭제
                                    deleteCourseAssignmentRepository.deleteById(findAssignmentId.getAssignmentId());

                                }

                            }

                             // ----------------------------------------------------------------------------

                             // 해당 과목의 게시판이 있는지 확인
                            Optional<DeleteCourseBoard> findBoardId = deleteCourseBoardRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                            if(findBoardId.isPresent()){

                                List<DeleteCourseBoardPost> findPostIds = deleteCourseBoardPostRepository.findByBoardId(findBoardId.get().getBoardId());

                                if(!findPostIds.isEmpty()){

                                    for(DeleteCourseBoardPost findPostId : findPostIds){

                                        // 해당 게시판에 댓글이 있는지 확인
                                        List<DeleteCourseComment> findCommentIds = deleteCourseCommentRepository.findByPostId(findPostId.getPostId());

                                        if(!findCommentIds.isEmpty()){

                                            for(DeleteCourseComment findCommentId : findCommentIds){
                                                // 해당 게시글의 댓글 삭제
                                                deleteCourseCommentRepository.deleteById(findCommentId.getCommentId());

                                            }

                                        }

                                        if(!findPostId.getFileNo().isEmpty()){

                                            // 해당 게시판의 첨부된 파일이 있으면 삭제
                                            deleteCourseFileinfoRepository.deleteById(findPostId.getFileNo());

                                        }

                                        // 해당 게시판의 게시글 삭제
                                        deleteCourseBoardPostRepository.deleteById(findPostId.getPostId());

                                    }

                                }

                                // 해당 과목의 게시판 삭제
                                deleteCourseBoardRepository.deleteById(findBoardId.get().getBoardId());

                            }

                            // ----------------------------------------------------------------------------

                            // 해당 과목을 듣고 있는 학생이 있는지 확인 
                            List<DeleteCourseUserOwnAssignment> findIncreaseIds = deleteCourseUserOwnAssignmentRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                            if(!findIncreaseIds.isEmpty()){

                                for(DeleteCourseUserOwnAssignment findIncreaseId : findIncreaseIds){

                                    // 사용자별 과목 목록 삭제
                                    deleteCourseUserOwnAssignmentRepository.deleteById(findIncreaseId.getIncreaseId());

                                }

                            }

                        }

                        // ----------------------------------------------------------------------------

                        // 해당 과정의 과목에 대한 개설과목 코드 삭제
                        deleteCourseOfferedSubjectsRepository.deleteById(findOfferedSubjectId.get().getOfferedSubjectsId());

                        // 과정별 과목 목록에서 과목 삭제
                        deleteCourseOwnSubjectRepository.deleteById(findSubjectId.getIncreaseId());

                        // 과정의 과목 삭제
                        deleteCourseSubjectRepository.deleteById(findSubjectId.getSubjectId());

                    }

                    // 해당 과정을 듣고 있는 사람이 있는지 확인
                    List<DeleteCourseUserOwnCourse> findUserOwnCourseIncreaseIds = deleteCourseUserOwnCourseRepository.findByCourseIdAndOfficerSessionId(courseId, sessionId);

                    if(!findUserOwnCourseIncreaseIds.isEmpty()){

                        for(DeleteCourseUserOwnCourse findUserOwnCourseIncreaseId : findUserOwnCourseIncreaseIds){

                            deleteCourseUserOwnCourseRepository.deleteById(findUserOwnCourseIncreaseId.getIncreaseId());

                        }

                    }

                    deleteCourseRepository.deleteById(findCourse.get().getCourseId());
                    
                    return ResponseEntity.ok().body(findCourse.get().getCourseTitle()+"과정 삭제 완료");

                }else{
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("삭제 할 수 없습니다.");
                }
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
            }
        
    }
    
    
    
    
    
}
