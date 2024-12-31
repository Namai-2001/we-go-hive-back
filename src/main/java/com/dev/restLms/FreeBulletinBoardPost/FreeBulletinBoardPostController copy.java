// package com.dev.restLms.FreeBulletinBoardPost;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.dev.restLms.entity.BoardPost;
// import com.dev.restLms.entity.Comment;
// import com.dev.restLms.entity.UserOwnPermissionGroup;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
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
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.GetMapping;



// @RestController
// @RequestMapping("/freeBulletinBoardPost")
// @Tag(name = "FreeBulletinBoardPostController", description = "자유게시판")
// public class FreeBulletinBoardPostController {

//     @Autowired
//     FreeBulletinBoardPostRepository freeBulletinBoardPostRepository;

//     @Autowired
//     FreeBulletinBoardPostCommentRepository freeBulletinBoardPostCommentRepository;

//     @Autowired
//     FreeBulletinBoardPostUserRepository freeBulletinBoardPostUserRepository;

//     @Autowired
//     FreeBulletinBoardPostUserOwnPermissionGroupRepository freeBulletinBoardPostUserOwnPermissionGroupRepository;

//     @Autowired
//     FreeBulletinBoardPostPermissionGroupRepository freeBulletinBoardPostPermissionGroupRepository;

//     @PostMapping("/post")
//     @Operation(summary = "자유게시판 게시글 작성", description = "자유게시판에서 게시글을 작성합니다.")
//     public ResponseEntity<?> postBoardPost(
//         @RequestParam String sessionId,
//         @RequestParam String boardId,
//         @RequestBody BoardPost userBoardPost
//         ) {

//             Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(user.isPresent()){
                
//                 // 작성자의 닉네임 확인 
//                 Optional<FreeBulletinBoardPostUser> findUserNickName = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);
    
//                 userBoardPost.setAuthorNickname(findUserNickName.get().getNickname());
//                 // PostId는 UUID로 자동 할당받기 때문에 null로 삽입 
//                 userBoardPost.setPostId(null);
//                 userBoardPost.setSessionId(sessionId);
//                 userBoardPost.setBoardId(boardId);
//                 userBoardPost.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//                 // 공지사항이면 T 아니면 F 
//                 if(userBoardPost.getIsNotice().equals("true")){
//                     userBoardPost.setIsNotice("T");
//                 }else{
//                     userBoardPost.setIsNotice("F");
//                 }
//                 // 파일 첨부는 일단 보류 
//                 userBoardPost.setFileNo(null);
    
//                 BoardPost savePost = freeBulletinBoardPostRepository.save(userBoardPost);
            
//             return ResponseEntity.ok().body(savePost);

//             }

//             return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");

//     }

//     @PostMapping("/comment")
//     @Operation(summary = "자유게시판 게시글의 댓글 작성", description = "자유게시판의 게시글에서 댓글을 작성합니다.")
//     public ResponseEntity<?> postComment(
//         @RequestParam String sessionId,
//         @RequestParam String postId,
//         @RequestBody Comment userComment
//     ) {

//         Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//         if(user.isPresent()){

//             // 작석자의 닉네임 확인 
//             Optional<FreeBulletinBoardPostUser> findUserNickname = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);
    
//             userComment.setAuthorNickname(findUserNickname.get().getNickname());
//             userComment.setCommentId(null);
//             userComment.setPostId(postId);
//             userComment.setSessionId(sessionId);
//             userComment.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//             // 비밀글인지 확인 
//             if(userComment.getIsSecret().equals("true")){
//                 userComment.setIsSecret("T");
//             }else{
//                 userComment.setIsSecret("F");
//             }
//             userComment.setPreviousCommentId(null);
            
//             Comment saveComment = freeBulletinBoardPostCommentRepository.save(userComment);
            
//             userComment.setRootCommentId(userComment.getCommentId());
//             freeBulletinBoardPostCommentRepository.save(saveComment);
    
//             return ResponseEntity.ok().body(saveComment);

//         }

//         return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");

//     }

//     @PostMapping("/comment/reply")
//     @Operation(summary = "자유게시판 게시글 댓글의 대댓글 작성", description = "자유게시판의 게시글 댓글의 대댓글을 작성합니다.")
//     public ResponseEntity<?> postCommentReply(
//         @RequestParam String sessionId,
//         @RequestParam String postId,
//         @RequestParam String commentId,
//         @RequestBody Comment userCommentReply
//         ) {

//             Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(user.isPresent()){

//                 // 작성자의 닉네임 확인 
//                 Optional<FreeBulletinBoardPostUser> findUserNickname = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);
    
//                 userCommentReply.setAuthorNickname(findUserNickname.get().getNickname());
//                 userCommentReply.setCommentId(null);
//                 userCommentReply.setSessionId(sessionId);
//                 userCommentReply.setPostId(postId);
//                 userCommentReply.setRootCommentId(commentId);
//                 userCommentReply.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//                 // 비밀글인지 확인 
//                 if(userCommentReply.getIsSecret().equals("true")){
//                     userCommentReply.setIsSecret("T");
//                 }else{
//                     userCommentReply.setIsSecret("F");
//                 }
    
//                 // 해당 댓글의 목록 가져오기 
//                 List<Comment> commentReplys = freeBulletinBoardPostCommentRepository.findByRootCommentId(commentId);
    
//                 // 작성한 댓글의 이전 댓글의 고유 코드를 찾기 
//                 Long createdDate = 0L;
//                 String previousId = null;
//                 for(Comment commentReply : commentReplys){
//                     String commentCreatedDate = commentReply.getCreatedDate();
//                     if(createdDate < Long.parseLong(commentCreatedDate)){
//                         createdDate = Long.parseLong(commentCreatedDate);
//                         previousId = commentReply.getCommentId();
//                     }
//                 }
//                 userCommentReply.setPreviousCommentId(previousId);
    
//                 Comment saveCommentReply = freeBulletinBoardPostCommentRepository.save(userCommentReply);
    
//             return ResponseEntity.ok().body(saveCommentReply);

//             }

//             return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");

//     }
    
//     @GetMapping()
//     @Operation(summary = "자유게시판 게시글 및 댓글 확인", description = "자유게시판의 게시글과 댓글 목록을 가져옵니다.")
//     public ResponseEntity<?> getboardPost(
//         @RequestParam String sessionId,
//         @RequestParam String postId,
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "5") int size
//         ) {

//             Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(user.isPresent()){

//                 // 해당 게시글 내용 확인 
//                 Optional<FreeBulletinBoardPosts> boardPost = freeBulletinBoardPostRepository.findByPostId(postId);

//                 Map<String, Object> resuList = new HashMap<>();

//                 HashMap<String, Object> posts = new HashMap<>();
//                 posts.put("postSessionId", boardPost.get().getSessionId());
//                 posts.put("postId", boardPost.get().getPostId());
//                 posts.put("postAuthorNickname", boardPost.get().getAuthorNickname());
//                 posts.put("postCreatedDate", boardPost.get().getCreatedDate());
//                 posts.put("postTitle", boardPost.get().getTitle());
//                 posts.put("postContent", boardPost.get().getContent());

//                 resuList.put("post", posts);

//                 // 해당 게시글의 부모댓글들 확인 
//                 Pageable pageable = PageRequest.of(page, size);
//                 Page<Comment> comments = freeBulletinBoardPostCommentRepository.findByPostIdAndPreviousCommentId(postId, null, pageable);

//                 List<Map<String, Object>> userComments = new ArrayList<>();

//                 for(Comment comment : comments){

//                     HashMap<String, Object> userComment = new HashMap<>();
//                     userComment.put("commentId", comment.getCommentId());
//                     userComment.put("commentAuthorNickname", comment.getAuthorNickname());
//                     userComment.put("commentCreatedDate", comment.getCreatedDate());
//                     userComment.put("comment", comment.getContent());
//                     userComment.put("commentSessionId", comment.getSessionId());
//                     userComment.put("isSecret", comment.getIsSecret());
                    
//                     userComments.add(userComment);

//                 }

//                 resuList.put("comments", userComments);

//                 Map<String, Object> response = new HashMap<>();
//                 response.put("postAndComment", resuList);
//                 response.put("currentPage", comments.getNumber());
//                 response.put("totalItems", comments.getTotalElements());
//                 response.put("totalPages", comments.getTotalPages());

//                 return ResponseEntity.ok().body(response);

//             }
            
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접속 가능");
//     }

//     @GetMapping("/reply")
//     @Operation(summary = "자유게시판 댓글의 대댓글 확인", description = "자유게시판의 대댓글 목록을 가져옵니다.")
//     public ResponseEntity<?> getCommentReply(
//         @RequestParam String sessionId,
//         @RequestParam String commentId,
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "6") int size
//         ) {

//             // 사용자 확인 
//             Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(user.isPresent()){

//                 // 해당 댓글의 대댓글 목록 확인 
//                 Pageable pageable = PageRequest.of(page, size);
//                 Page<Comment> replyComments = freeBulletinBoardPostCommentRepository.findByRootCommentId(commentId, pageable);
    
//                 List<Map<String, Object>> resultList = new ArrayList<>();
    
//                 for(Comment replyComment : replyComments) {
    
//                     if(replyComment.getPreviousCommentId() != null){
    
    
//                         HashMap<String, Object> comment = new HashMap<>();
//                         comment.put("replyCommentId", replyComment.getCommentId());
//                         comment.put("replyAuthorNickname", replyComment.getAuthorNickname());
//                         comment.put("replyCreatedDate", replyComment.getCreatedDate());
//                         comment.put("replyContent", replyComment.getContent());
//                         comment.put("replySessionId", replyComment.getSessionId());
//                         comment.put("replyIsSecret", replyComment.getIsSecret());
    
//                         resultList.add(comment);
    
//                     }
                    
//                 }

//             Map<String, Object> response = new HashMap<>();
//             response.put("replyComment", resultList);
//             response.put("currentPage", replyComments.getNumber());
//             response.put("totalItems", replyComments.getTotalElements());
//             response.put("totalPages", replyComments.getTotalPages());
    
//             return ResponseEntity.ok().body(response);

//             }

//             return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접속 가능");

//     }

//     @PostMapping("/postDelete")
//     @Operation(summary = "자유게시판 게시글 삭제", description = "자유게시판의 게시글을 삭제합니다")
//     public ResponseEntity<?> deletePost(
//         @RequestParam String sessionId,
//         @RequestParam String postId
//         ) {
//             // 삭제하려는 게시글의 사용자 세션아이디 확인 
//             Optional<FreeBulletinBoardPosts> findUser = freeBulletinBoardPostRepository.findByPostId(postId);

//             // 삭제하려는 사용자의 권한 확인 
//             Optional<UserOwnPermissionGroup> findOfficer = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             // 권한 이름이 OFFICER 또는 SITE_OFFICER인지 확인
//             Optional<FreeBulletinBoardPostPermissionGroup> findOfficerName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(findOfficer.get().getPermissionGroupUuid2());

//             String permissionName = findOfficerName.get().getPermissionName();

//             if(findUser.get().getSessionId().equals(sessionId) || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER")){

//                 freeBulletinBoardPostRepository.deleteById(postId);
//                 return ResponseEntity.ok().body("삭제 완료");

//             }
        
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
//     }

//     @PostMapping("/postComment")
//     @Operation(summary = "자유게시판 게시글의 댓글 삭제", description = "자유게시판의 게시글의 댓글을 삭제합니다")
//     public ResponseEntity<?> deleteComment(
//         @RequestParam String sessionId,
//         @RequestParam String commentId
//         ) {
            
//             // 삭제하려는 댓글의 사용자 세션 아이디 확인 
//             Optional<Comment> findUser = freeBulletinBoardPostCommentRepository.findByCommentId(commentId);

//             // 삭제하려는 사용자의 권한 확인 
//             Optional<UserOwnPermissionGroup> findOfficer = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             // 권한 이름이 OFFICER 또는 SITE_OFFICER인지 확인
//             Optional<FreeBulletinBoardPostPermissionGroup> findOfficerName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(findOfficer.get().getPermissionGroupUuid2());

//             String permissionName = findOfficerName.get().getPermissionName();

//             if(findUser.get().getSessionId().equals(sessionId) || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER")){

//                 freeBulletinBoardPostCommentRepository.deleteById(commentId);
//                 return ResponseEntity.ok().body("삭제 완료");

//             }
        
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
//     }
// }
