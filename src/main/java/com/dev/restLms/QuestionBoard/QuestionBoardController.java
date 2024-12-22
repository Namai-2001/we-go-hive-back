package com.dev.restLms.QuestionBoard;

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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/quesionBoard")
@Tag(name = "QuestionBoardController", description = "과목별 질문 게시판")
public class QuestionBoardController {

    @Autowired
    private QuestionBoardRepository questionBoardRepository;

    @Autowired
    private QuestionBoardPostRepository questionBoardPostRepository;

    @Autowired
    private QuestionBoardOfferedSubjectsRepository questionBoardOfferedSubjectsRepository;

    @Autowired
    private QuestionBoardUserOwnAssignmentRepository questionBoardUserOwnAssignmentRepository;

    @Autowired
    private QuestionBoardCourseOwnSubjectRepository questionBoardCourseOwnSubjectRepository;

    @Autowired
    private QuestionBoardSubjectRepository questionBoardSubjectRepository;

    @Autowired
    private QuestionBoardUserOwnPermissionGroupRepository questionBoardUserOwnPermissionGroupRepository;

    @Autowired
    private QuestionBoardPermissionGroupRepository questionBoardPermissionGroupRepository;

    @GetMapping()
    @Operation(summary = "해당 질문 게시판의 게시글 제목, 사용자, 작성날짜", description = "해당 질문 게시판에 대한 게시글 목록을 반환합니다")
    public ResponseEntity<?> getAllSubjectQuestion(
        @RequestParam String offeredSubjectsId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

        // 권한 확인
        Optional<QuestionBoardUserOwnPermissionGroup> userOwnPermissionGroup = questionBoardUserOwnPermissionGroupRepository.findBySessionId(sessionId);

        // 해당 과목의 게시글이 있는지 확인
        Optional<QuestionBoard> questionBoard = questionBoardRepository.findByOfferedSubjectsId(offeredSubjectsId);

        if(userOwnPermissionGroup.isPresent()||questionBoard.isPresent()){

            String permissionId = userOwnPermissionGroup.get().getPermissionGroupUuid2();

            // 권한 아이디 확인
            Optional<QuestionBoardPermissionGroup> permissionGroup = questionBoardPermissionGroupRepository.findByPermissionGroupUuid(permissionId);

            String permissionGroupName = permissionGroup.get().getPermissionName();

            switch (permissionGroupName) {
                case "SITE_OFFICER" , "OFFICER":
                    Map<String, Object> response1 = saveboardPost(sessionId, offeredSubjectsId, page, size);
                    return ResponseEntity.ok().body(response1);

                case "COURSE_OFFICER":
                    // 해당 과정의 책임자인지 확인
                    Optional<QuestionBoardOfferedSubjects> courseOfficerCheck = questionBoardOfferedSubjectsRepository.findByOfferedSubjectsIdAndOfficerSessionId(offeredSubjectsId, sessionId);
                    if(courseOfficerCheck.isPresent()){
                        Map<String, Object> response2 = saveboardPost(sessionId, offeredSubjectsId, page, size);
                        return ResponseEntity.ok().body(response2);
                    }else{
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과정의 책임자가 아닙니다");
                    }
                case "TEACHER":
                    // 해당 과목의 강사인지 확인
                    Optional<QuestionBoardOfferedSubjects> teacherSessionIdCheck = questionBoardOfferedSubjectsRepository.findByOfferedSubjectsIdAndTeacherSessionId(offeredSubjectsId, sessionId);
                    if(teacherSessionIdCheck.isPresent()){
                        Map<String, Object> response3 = saveboardPost(sessionId, offeredSubjectsId, page, size);
                        return ResponseEntity.ok().body(response3);
                    }else{
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과정의 강사가 아닙니다");
                    }
                case "STUDENT" :
                    // 해당 과목의 수강생인지 확인
                    Optional<QuestionBoardUserOwnAssignment> userSessionCheck = questionBoardUserOwnAssignmentRepository.findByOfferedSubjectsIdAndUserSessionId(offeredSubjectsId, sessionId);
                    if(userSessionCheck.isPresent()){
                        Map<String, Object> response4 = saveboardPost(sessionId, offeredSubjectsId, page, size);
                        return ResponseEntity.ok().body(response4);
                    }else{
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과정의 수강생이 아닙니다");
                    }
            
                default:
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
            }
                
        }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과목의 게시판이 없습니다.");
    }

    private Map<String, Object> saveboardPost(String sessionId, String offeredSubjectsId, int page, int size) {

        // 개설과목의 게시판아이디 확인하기 위함
        Optional<QuestionBoard> questionBoard = questionBoardRepository.findByOfferedSubjectsId(offeredSubjectsId);

        List<Map<String, Object>> resultList = new ArrayList<>();
    
        // 페이징 처리된 게시글 가져오기
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<QuestionBoardPost> questionBoardPosts =
            questionBoardPostRepository.findByBoardId(questionBoard.get().getBoardId(), pageable);
    
        // 해당 과목의 개설과목코드를 통한 선생님 세션아이디 가져오기 위함
        Optional<QuestionBoardOfferedSubjects> offeredSubjects =
            questionBoardOfferedSubjectsRepository.findByOfferedSubjectsId(offeredSubjectsId);
    
        // 개설과목 테이블에서 개설과목코드를 통해 과목코드 확인
        Optional<QuestionBoardCourseOwnSubject> courseSubject =
            questionBoardCourseOwnSubjectRepository.findBySubjectId(offeredSubjects.get().getSubjectId());
    
        // 과목테이블에 과목코드를 통해 과목이름 확인
        Optional<QuestionBoardSubject> subject =
            questionBoardSubjectRepository.findBySubjectId(courseSubject.get().getSubjectId());
    
        for (QuestionBoardPost post : questionBoardPosts) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("sessionId", post.getSessionId());
            postMap.put("postId", post.getPostId());
            postMap.put("title", post.getTitle());
            postMap.put("authorNickname", post.getAuthorNickname());
            postMap.put("createdDate", post.getCreatedDate());
            postMap.put("isNotice", post.getIsNotice());
            postMap.put("teacherSessionId", offeredSubjects.get().getTeacherSessionId());
            postMap.put("boardCategory", questionBoard.get().getBoardCategory());
            postMap.put("subjectName", subject.get().getSubjectName());
            postMap.put("boardId", questionBoard.get().getBoardId());
            resultList.add(postMap);
        }
    
        Map<String, Object> response = new HashMap<>();
        response.put("posts", resultList);
        response.put("currentPage", questionBoardPosts.getNumber());
        response.put("totalItems", questionBoardPosts.getTotalElements());
        response.put("totalPages", questionBoardPosts.getTotalPages());

        return response;

    }
    
}
