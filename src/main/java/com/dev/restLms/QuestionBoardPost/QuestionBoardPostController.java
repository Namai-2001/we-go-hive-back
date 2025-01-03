package com.dev.restLms.QuestionBoardPost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.OfficerMainPage.projection.OfficerMainPagePermissionGroup;
import com.dev.restLms.entity.BoardPost;
import com.dev.restLms.entity.Comment;
import com.dev.restLms.entity.UserOwnPermissionGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/quesionBoard")
@Tag(name = "QuestionBoardPostController", description = "과목별 질문 게시판의 게시글")
public class QuestionBoardPostController {

    @Autowired
    private QuestionBoardPostBoardPostRepository questionBoardPostBoardPostRepository;

    @Autowired
    private QuestionBoardPostCommentRepository questionBoardPostCommentRepository;

    @Autowired
    private QuestionBoardPostPermissionGroupRepository questionBoardPostPermissionGroupRepository;

    @Autowired
    private QuestionBoardPostUserOwnPermissionGroupRepository questionBoardPostUserOwnPermissionGroupRepository;

    @Autowired
    private QuestionBoardPostBoardRepository questionBoardPostBoardRepository;

    @Autowired
    private QuestionBoardPostUserOwnAssignmentRepository questionBoardPostUserOwnAssignmentRepository;

    @Autowired
    private QuestionBoardPostUserRepository questionBoardPostUserRepository;
    

    @GetMapping("/boradPost")
    @Operation(summary = "사용자의 질문과 답변", description = "질문과 답변을 반환합니다")
    public ResponseEntity<?> getboardPost(
        @RequestParam String postId
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            // 수강생 권한 확인
            Optional<QuestionBoardPostUserOwnPermissionGroup> userOwnPermissionGroup = questionBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            // 수강생 권한 이름 확인
            Optional<QuestionBoardPostPermissionGroup> permissionGroup = questionBoardPostPermissionGroupRepository.findByPermissionGroupUuid(userOwnPermissionGroup.get().getPermissionGroupUuid2());

            String permissionName = permissionGroup.get().getPermissionName();

            // 게시글의 세션아이디를 확인하기 위함함
            Optional<QuestionBoardPostBoardPost> boardPost = questionBoardPostBoardPostRepository.findByPostId(postId);

            if(permissionName.equals("STUDENT") && boardPost.get().getIsNotice().equals("F")){
                // 사용자의 세션 아이디와 게시글 작성자의 세션아이디가 같은지 확인하기 위함함
                Optional<QuestionBoardPostBoardPost> userCheck = questionBoardPostBoardPostRepository.findBySessionIdAndPostId(sessionId, postId);
                if(userCheck.isEmpty()){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("비밀글 입니다");
                }
            }

            // 해당 게시글의 댓글을 확인
            Optional<QuestionBoardPostComment> comment = questionBoardPostCommentRepository.findByPostId(boardPost.get().getPostId());

            if(boardPost.isPresent()){

                 // 결과를 저장할 리스트
                List<Map<String, String>> resultList = new ArrayList<>();

                HashMap<String, String> permission = new HashMap<>();
                permission.put("SignInPermissionName", permissionName);

                resultList.add(permission);

                HashMap<String, String> posts = new HashMap<>();
                posts.put("postSessionId", boardPost.get().getSessionId());
                posts.put("postID", boardPost.get().getPostId());
                posts.put("postAuthorNickname", boardPost.get().getAuthorNickname());
                posts.put("postCreatedDate", boardPost.get().getCreatedDate());
                posts.put("postTitle", boardPost.get().getTitle());
                posts.put("postContent", boardPost.get().getContent());

                resultList.add(posts);

                if(comment.isPresent()){
                    
                    HashMap<String, String> comments = new HashMap<>();
                    comments.put("commentId", comment.get().getCommentId());
                    comments.put("commentAuthorNickname", comment.get().getAuthorNickname());
                    comments.put("commentCreatedDate", comment.get().getCreatedDate());
                    comments.put("comment", comment.get().getContent());
                    comments.put("commentSessionId", comment.get().getSessionId());
    
                    resultList.add(comments);

                }

                return ResponseEntity.ok().body(resultList);

            }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("비밀글 입니다.");
    }

    @GetMapping("/postCheck")
    @Operation(summary = "질문게시판 권한")
    public ResponseEntity<?> getPermissionCheck(
        @RequestParam String offeredSubjectId
    ){

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
        .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String sessionId = auth.getPrincipal().toString();

        Optional<QuestionBoardPostUserOwnPermissionGroup> permissionCheck = questionBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

        Optional<QuestionBoardPostPermissionGroup> permissionNameCheck = questionBoardPostPermissionGroupRepository.findByPermissionGroupUuid(permissionCheck.get().getPermissionGroupUuid2());

        String permissionName = permissionNameCheck.get().getPermissionName();

        if(permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER") || permissionName.equals("INDIV_OFFICER")){
            return ResponseEntity.ok().body("작성 권한이 확인되었습니다.");
        }else if(permissionName.equals("TEACHER")){
            Optional<QuestionBoardPostBoard> findTeacherSessionId = questionBoardPostBoardRepository.findByTeacherSessionIdAndOfferedSubjectsId(sessionId, offeredSubjectId);
            if(findTeacherSessionId.isPresent()){
                return ResponseEntity.ok().body("작성 권한이 확인되었습니다.");
            }else{
                return ResponseEntity.ok().body("작성 권한이 없습니다.");
            }
        }else if(permissionName.equals("STUDENT")){
            Optional<QuestionBoardPostUserOwnAssignment> findUser = questionBoardPostUserOwnAssignmentRepository.findByUserSessionIdAndOfferedSubjectsId(sessionId, offeredSubjectId);
            if(findUser.isPresent()){
                return ResponseEntity.ok().body("작성 권한이 확인되었습니다.");
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("작성 권한이 없습니다.");
            }
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("작성 권한이 없습니다.");
        }

    }

    @PostMapping("/post")
    @Operation(summary = "질문 게시판 게시글 작성", description = "해당 과목의 질문 게시글을 작성합니다")
    public ResponseEntity<?> postQustionBoard(
        @RequestParam String boardId,
        @RequestBody BoardPost qustionBoardPost
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            // 사용자 확인 
            Optional<QuestionBoardPostUserOwnPermissionGroup> userCheck = questionBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            if(userCheck.isEmpty()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용해 주세요");
            }

            // 사용자 권한 확인 
            Optional<QuestionBoardPostPermissionGroup> permissionCheck = questionBoardPostPermissionGroupRepository.findByPermissionGroupUuid(userCheck.get().getPermissionGroupUuid2());
            String userPerssionName = permissionCheck.get().getPermissionName();
            
            // 게시글의 개설과목 코드 확인 
            Optional<QuestionBoardPostBoard>  findBoard = questionBoardPostBoardRepository.findByBoardId(boardId);

            boolean permissionNameCheck = false;

            if(userPerssionName.equals("OFFICER") || userPerssionName.equals("SITE_OFFICER") || userPerssionName.equals("INDIV_OFFICER")){
                permissionNameCheck = true;
            }

            if(userPerssionName.equals("TEACHER")){
                // 해당 과목의 강사인지 확인 
                Optional<QuestionBoardPostBoard> findTeacher = questionBoardPostBoardRepository.findByTeacherSessionIdAndOfferedSubjectsId(sessionId, findBoard.get().getOfferedSubjectsId());

                if(findTeacher.isPresent()){
                    permissionNameCheck = true;
                }

            }

            if(userPerssionName.equals("STUDENT")){
                // 해당 과목의 학생인지 확인 
                Optional<QuestionBoardPostUserOwnAssignment> findSudent = questionBoardPostUserOwnAssignmentRepository.findByUserSessionIdAndOfferedSubjectsId(sessionId, findBoard.get().getOfferedSubjectsId());

                if(findSudent.isPresent()){
                    permissionNameCheck = true;
                }
            }

            if(permissionNameCheck){

                Optional<QuestionBoardPostUser> findUserNickName = questionBoardPostUserRepository.findBySessionId(sessionId);

                qustionBoardPost.setAuthorNickname(findUserNickName.get().getNickname());
                qustionBoardPost.setPostId(null);
                qustionBoardPost.setSessionId(sessionId);
                qustionBoardPost.setBoardId(boardId);
                qustionBoardPost.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                if(qustionBoardPost.getIsNotice().equals("true")){
                    qustionBoardPost.setIsNotice("T");
                }else{
                    qustionBoardPost.setIsNotice("F");
                }
                qustionBoardPost.setFileNo(null);

                BoardPost savePost = questionBoardPostBoardPostRepository.save(qustionBoardPost);

                return ResponseEntity.ok().body(savePost);

            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
            }
    }

    @PostMapping("/comment")
    @Operation(summary = "질문게시판 게시글의 댓글 작성", description = "질문게시판의 게시글에서 댓글을 작성합니다.")
    public ResponseEntity<?> postComment(
        @RequestParam String postId,
        @RequestBody Comment postComment
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            Optional<QuestionBoardPostUserOwnPermissionGroup> userCheck = questionBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            Optional<QuestionBoardPostPermissionGroup> permissionCheck = questionBoardPostPermissionGroupRepository.findByPermissionGroupUuid(userCheck.get().getPermissionGroupUuid2());

            String permissionName  = permissionCheck.get().getPermissionName();

            if(permissionName.equals("TEACHER") || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER") || permissionName.equals("INDIV_OFFICER")){

                Optional<QuestionBoardPostUser> findUserNickname = questionBoardPostUserRepository.findBySessionId(sessionId);

                postComment.setSessionId(sessionId);
                postComment.setAuthorNickname(findUserNickname.get().getNickname());
                postComment.setCommentId(null);
                postComment.setPostId(postId);
                postComment.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                postComment.setIsSecret("F");
                postComment.setPreviousCommentId(null);
                postComment.setRootCommentId("null");

                Comment saveComment = questionBoardPostCommentRepository.save(postComment);

                return ResponseEntity.ok().body(saveComment);

            }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
    }

    @PostMapping("/deletePost")
    @Operation(summary = "질문게시판 게시글 삭제", description = "질문게시판의 게시글을 삭제합니다")
    public ResponseEntity<?> deletePost(
        @RequestParam String postId
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            Optional<QuestionBoardPostBoardPost> findUser = questionBoardPostBoardPostRepository.findByPostId(postId);

            Optional<QuestionBoardPostUserOwnPermissionGroup> findOfficer = questionBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            Optional<QuestionBoardPostPermissionGroup> findPermissionName = questionBoardPostPermissionGroupRepository.findByPermissionGroupUuid(findOfficer.get().getPermissionGroupUuid2());

            String permissionName = findPermissionName.get().getPermissionName();

            boolean permissionCheck = false;

            if(permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER") || permissionName.equals("INDIV_OFFICER")){
                permissionCheck = true;
            }

            if(permissionName.equals("TEACHER")){

                Optional<QuestionBoardPostBoardPost> findBoardId = questionBoardPostBoardPostRepository.findByPostId(postId);

                Optional<QuestionBoardPostBoard> findTeacherSessionId = questionBoardPostBoardRepository.findByBoardId(findBoardId.get().getBoardId());

                if(findTeacherSessionId.get().getTeacherSessionId().equals(sessionId)){
                    permissionCheck = true;
                }

            }

            if(findUser.get().getSessionId().equals(sessionId)){
                permissionCheck = true;
            }

            if(permissionCheck){

                Optional<QuestionBoardPostComment> deleteComment = questionBoardPostCommentRepository.findByPostId(postId);

                if(deleteComment.isPresent()){
                    questionBoardPostCommentRepository.deleteById(deleteComment.get().getCommentId());
                }

                questionBoardPostBoardPostRepository.deleteById(postId);
                return ResponseEntity.ok().body("삭제 완료");

            }else{

                return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");

            }
        
    }

    @PostMapping("/deleteComment")
    @Operation(summary = "질문게시판 게시글의 댓글 삭제", description = "질문게시판의 게시글의 댓글을 삭제합니다")
    public ResponseEntity<?> deleteComment(
        @RequestParam String commentId
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();
        
            Optional<QuestionBoardPostUserOwnPermissionGroup> userCheck = questionBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            Optional<QuestionBoardPostPermissionGroup> permissionCheck = questionBoardPostPermissionGroupRepository.findByPermissionGroupUuid(userCheck.get().getPermissionGroupUuid2());

            String permissionName  = permissionCheck.get().getPermissionName();

            if(permissionName.equals("TEACHER") || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER") || permissionName.equals("INDIV_OFFICER")){

                questionBoardPostCommentRepository.deleteById(commentId);
                return ResponseEntity.ok().body("삭제 완료");

            }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
    }
    
    
}
