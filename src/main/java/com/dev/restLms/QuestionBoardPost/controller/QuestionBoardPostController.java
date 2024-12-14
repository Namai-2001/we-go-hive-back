package com.dev.restLms.QuestionBoardPost.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostBoardPost;
import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostComment;
import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostPermissionGroup;
import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostUserOwnPermissionGroup;
import com.dev.restLms.QuestionBoardPost.persistence.QuestionBoardPostBoardPostRepository;
import com.dev.restLms.QuestionBoardPost.persistence.QuestionBoardPostCommentRepository;
import com.dev.restLms.QuestionBoardPost.persistence.QuestionBoardPostPermissionGroupRepository;
import com.dev.restLms.QuestionBoardPost.persistence.QuestionBoardPostUserOwnPermissionGroupRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/boradPost")
    @Operation(summary = "사용자의 질문과 답변", description = "질문과 답변을 반환합니다")
    public ResponseEntity<?> getboardPost(
        @RequestParam String sessionId,
        @RequestParam String postId
        ) {

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
    
    
}
