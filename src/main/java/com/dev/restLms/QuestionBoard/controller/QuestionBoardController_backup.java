// package com.dev.restLms.QuestionBoard.controller;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.dev.restLms.QuestionBoard.model.QuestionBoard;
// import com.dev.restLms.QuestionBoard.model.QuestionBoardCourseOwnSubject;
// import com.dev.restLms.QuestionBoard.model.QuestionBoardOfferedSubjects;
// import com.dev.restLms.QuestionBoard.model.QuestionBoardPost;
// import com.dev.restLms.QuestionBoard.model.QuestionBoardSubject;
// import com.dev.restLms.QuestionBoard.model.QuestionBoardUserOwnAssignment;
// import com.dev.restLms.QuestionBoard.persistence.QuestionBoardCourseOwnSubjectRepository;
// import com.dev.restLms.QuestionBoard.persistence.QuestionBoardOfferedSubjectsRepository;
// import com.dev.restLms.QuestionBoard.persistence.QuestionBoardPostRepository;
// import com.dev.restLms.QuestionBoard.persistence.QuestionBoardRepository;
// import com.dev.restLms.QuestionBoard.persistence.QuestionBoardSubjectRepository;
// import com.dev.restLms.QuestionBoard.persistence.QuestionBoardUserOwnAssignmentRepository;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;


// @RestController
// @RequestMapping("quesionBoard")
// @Tag(name = "QuestionBoardController", description = "과목별 질문 게시판")
// public class QuestionBoardController {

//     @Autowired
//     private QuestionBoardRepository questionBoardRepository;

//     @Autowired
//     private QuestionBoardPostRepository questionBoardPostRepository;

//     @Autowired
//     private QuestionBoardOfferedSubjectsRepository questionBoardOfferedSubjectsRepository;

//     @Autowired
//     private QuestionBoardUserOwnAssignmentRepository questionBoardUserOwnAssignmentRepository;

//     @Autowired
//     private QuestionBoardCourseOwnSubjectRepository questionBoardCourseOwnSubjectRepository;

//     @Autowired
//     private QuestionBoardSubjectRepository questionBoardSubjectRepository;

//     @GetMapping("/course")
//     @Operation(summary = "해당 질문 게시판의 게시글 제목, 사용자, 작성날짜", description = "해당 질문 게시판에 대한 게시글 목록을 반환합니다")
//     public ResponseEntity<?> getAllSubjectQuestion(
//         @RequestParam String sessionId,
//         @RequestParam String offeredSubjectsId
//         ) {

//             // 사용자가 해당 과목의 수강생인지 확인
//             Optional<QuestionBoardUserOwnAssignment> userOwnAssignment = questionBoardUserOwnAssignmentRepository.findByOfferedSubjectsIdAndUserSessionId(offeredSubjectsId, sessionId);

//             // 해당 과목의 질문 게시판이 존재하는지 확인
//             Optional<QuestionBoard> questionBoard = questionBoardRepository.findByOfferedSubjectsId(offeredSubjectsId);

//             // 해당 과목의 수강생이면서 해당 과목의 질문 게시판이 존재하는지 확인
//             if(userOwnAssignment.isPresent() && questionBoard.isPresent()){

//                 // 해당 과목의 게시글 목록 확인
//                 List<QuestionBoardPost> questionBoardPosts = questionBoardPostRepository.findByBoardId(questionBoard.get().getBoaedId());
                
//                 // 결과를 저장할 리스트
//                 List<Map<String, String>> resultList = new ArrayList<>();
                
//                 // 해당 과목의 개설과목코드를 통한 선생님 세션아이디 가져오기 위함
//                 Optional<QuestionBoardOfferedSubjects> questionBoardOfferedSubjects = questionBoardOfferedSubjectsRepository.findByOfferedSubjectsId(offeredSubjectsId);
                
//                 // // 개설과목 테이블에서 개설과목코드를 통해 과목코드 확인
//                 Optional<QuestionBoardCourseOwnSubject> courseOwnSubject = questionBoardCourseOwnSubjectRepository.findBySubjectId(questionBoardOfferedSubjects.get().getSubjectId());

//                 // // 과목테이블에 과목코드를 통해 과목이름 확인
//                 Optional<QuestionBoardSubject> subjectName = questionBoardSubjectRepository.findBySubjectId(courseOwnSubject.get().getSubjectId());

//                 for(QuestionBoardPost boardPost : questionBoardPosts){

//                     HashMap<String, String> postMap = new HashMap<>();
//                     postMap.put("sessionId", sessionId);
//                     postMap.put("postId", boardPost.getPostID());
//                     postMap.put("title", boardPost.getTitle());
//                     postMap.put("authorNickname", boardPost.getAuthorNickname());
//                     postMap.put("createdDate", boardPost.getCreatedDate());
//                     postMap.put("teacherSessionId", questionBoardOfferedSubjects.get().getTeacherSessionId());
//                     postMap.put("boardCategory", questionBoard.get().getBoardCategory());
//                     postMap.put("subjectName", subjectName.get().getSubjectName());
//                     postMap.put("boardId", questionBoard.get().getBoaedId());

//                     resultList.add(postMap);
    
//                 }
//                 return ResponseEntity.ok().body(resultList);
//             }
//             return ResponseEntity.status(HttpStatus.CONFLICT).body("과목 수강자가 아닙니다.");
//         }
        
    
// }
