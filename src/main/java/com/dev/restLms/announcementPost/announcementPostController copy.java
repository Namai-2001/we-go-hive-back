// package com.dev.restLms.announcementPost;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.dev.restLms.entity.BoardPost;
// import com.dev.restLms.entity.UserOwnPermissionGroup;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.GetMapping;



// @RestController
// @RequestMapping("/announcementPost")
// @Tag(name = "announcementPostController", description = "공지사항")
// public class announcementPostController {

//     @Autowired
//     AnnouncementPostBoardPostRepository announcementPostBoardPostRepository;

//     @Autowired
//     AnnouncementPostPermissionGroupRepository announcementPostPermissionGroupRepository;

//     @Autowired
//     AnnouncementPostUserRepository announcementPostUserRepository;

//     @Autowired
//     AnnouncementPostUserOwnPermissionGroupRepository announcementPostUserOwnPermissionGroupRepository;

//     @Autowired
//     AnnouncementPostFileInfoRepository announcementPostFileInfoRepository;

//     private static final String ROOT_DIR = "WeGoHiveFile/";
//     private static final String UPLOAD_DIR = "Board/";
//     private static final String BOARD_DIR = "NoticeBoard/";
//     private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

//     @PostMapping("/post")
//     @Operation(summary = "공지사항 작성", description = "공지사항을 작성합니다.")
//     public ResponseEntity<?> postAnnouncement(
//         @RequestParam String sessionId,
//         @RequestParam String boardId,
//         @RequestBody BoardPost officerBoardPost
//         ) {

//             // 회원 확인 
//             Optional<UserOwnPermissionGroup> officerSessionIdCheck = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(officerSessionIdCheck.isPresent()){

//                 // 권한 확인 
//                 Optional<announcementPostPermissionGroup> offcerCheck = announcementPostPermissionGroupRepository.findByPermissionGroupUuid(officerSessionIdCheck.get().getPermissionGroupUuid2());

//                 String officerName = offcerCheck.get().getPermissionName();

//                 if(officerName.equals("OFFICER") || officerName.equals("SITE_OFFICER")){

//                     // 관리자 닉네임 확인 
//                     Optional<announcementPostUser> findOfficerNickname = announcementPostUserRepository.findBySessionId(sessionId);

//                     officerBoardPost.setAuthorNickname(findOfficerNickname.get().getNickname());
//                     officerBoardPost.setPostId(null);
//                     officerBoardPost.setSessionId(sessionId);
//                     officerBoardPost.setBoardId(boardId);
//                     officerBoardPost.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//                     if(officerBoardPost.getIsNotice().equals("true")){
//                         officerBoardPost.setIsNotice("T");
//                     }else{
//                         officerBoardPost.setIsNotice("F");
//                     }
//                     officerBoardPost.setFileNo(null);

//                     BoardPost saveAnnouncementPost = announcementPostBoardPostRepository.save(officerBoardPost);

//                     return ResponseEntity.ok().body(saveAnnouncementPost);

//                 }

//                 return ResponseEntity.status(HttpStatus.CONFLICT).body("작성 권한이 없습니다.");

//             }
        
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");
//     }

//     @PostMapping("/postUpdate")
//     @Operation(summary = "공지사항 게시글 수정", description = "공지사항을 수정합니다.")
//     public ResponseEntity<?> updateAnnouncement(
//         @RequestParam String sessionId,
//         @RequestParam String postId,
//         @RequestBody BoardPost officerUpdatePost
//         ) {

//             Optional<UserOwnPermissionGroup> officerCheck = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(officerCheck.isPresent()){

//                 Optional<announcementPostPermissionGroup> officerNameCheck = announcementPostPermissionGroupRepository.findByPermissionGroupUuid(officerCheck.get().getPermissionGroupUuid2());

//                 String officerName = officerNameCheck.get().getPermissionName();

//                 if(officerName.equals("OFFICER") || officerName.equals("SITE_OFFICER")){

//                     Optional<announcementPostUser> findOfficerNickname = announcementPostUserRepository.findBySessionId(sessionId);

                    
//                     Optional<BoardPost> updatePost = announcementPostBoardPostRepository.findById(postId);
                    
//                     if(updatePost.isPresent()){
                        
//                         BoardPost postToUpdate = updatePost.get();
//                         postToUpdate.setAuthorNickname(findOfficerNickname.get().getNickname());
//                         postToUpdate.setSessionId(sessionId);
//                         postToUpdate.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//                         if(officerUpdatePost.getIsNotice().equals("true")){
//                             postToUpdate.setIsNotice("T");
//                         }else{
//                             postToUpdate.setIsNotice("F");
//                         }
//                         postToUpdate.setFileNo(null);
//                         postToUpdate.setTitle(officerUpdatePost.getTitle());
//                         postToUpdate.setContent(officerUpdatePost.getContent());

//                         announcementPostBoardPostRepository.save(postToUpdate);

//                         return ResponseEntity.ok().body("수정 완료");
                        
//                     }
//                     return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 게시물이 없습니다.");

//                 }

//             }
        
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("잘못된 접근입니다.");
//     }
    

//     @GetMapping()
//     @Operation(summary = "공지사항 확인", description = "공지사항 게시글을 가져옵니다.")
//     public ResponseEntity<?> getAnnouncement(
//         @RequestParam String sessionId,
//         @RequestParam String postId
//         ) {

//             Optional<UserOwnPermissionGroup> userCheck = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             if(userCheck.isPresent()){

//                 Optional<announcementPostBoardPost> announcementPost = announcementPostBoardPostRepository.findByPostId(postId);

//                 Map<String, String> post = new HashMap<>();
//                 post.put("postSessionId", announcementPost.get().getSessionId());
//                 post.put("postId", announcementPost.get().getPostId());
//                 post.put("postAuthorNickname", announcementPost.get().getAuthorNickname());
//                 post.put("postCreatedDate", announcementPost.get().getCreatedDate());
//                 post.put("postTitle", announcementPost.get().getTitle());
//                 post.put("postContent", announcementPost.get().getContent());
//                 post.put("postIsNotice", announcementPost.get().getIsNotice());
                
//                 return ResponseEntity.ok().body(post);

//             }

//         return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접근 가능");
//     }

//     @PostMapping("postDelete")
//     @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다")
//     public ResponseEntity<?> deleteAnnouncement(
//         @RequestParam String sessionId,
//         @RequestParam String postId 
//         ) {

//             Optional<UserOwnPermissionGroup> findOfficer = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//             Optional<announcementPostPermissionGroup> findOfficerName = announcementPostPermissionGroupRepository.findByPermissionGroupUuid(findOfficer.get().getPermissionGroupUuid2());

//             String officerPermission = findOfficerName.get().getPermissionName();

//             if(officerPermission.equals("OFFICER") || officerPermission.equals("SITE_OFFICER")){

//                 announcementPostBoardPostRepository.deleteById(postId);
//                 return ResponseEntity.ok().body("삭제 완료");

//             }
        
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
//     }
    
    
    
    
// }
