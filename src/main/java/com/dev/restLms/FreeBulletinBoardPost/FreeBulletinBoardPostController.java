package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.entity.BoardPost;
import com.dev.restLms.entity.Comment;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.UserOwnPermissionGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/freeBulletinBoardPost")
@Tag(name = "FreeBulletinBoardPostController", description = "자유게시판")
public class FreeBulletinBoardPostController {

    @Autowired
    FreeBulletinBoardPostRepository freeBulletinBoardPostRepository;

    @Autowired
    FreeBulletinBoardPostCommentRepository freeBulletinBoardPostCommentRepository;

    @Autowired
    FreeBulletinBoardPostUserRepository freeBulletinBoardPostUserRepository;

    @Autowired
    FreeBulletinBoardPostUserOwnPermissionGroupRepository freeBulletinBoardPostUserOwnPermissionGroupRepository;

    @Autowired
    FreeBulletinBoardPostPermissionGroupRepository freeBulletinBoardPostPermissionGroupRepository;

    @Autowired
    FreeBulletinBoardPostFileInfoRepository freeBulletinBoardPostFileInfoRepository;

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "Board/";
    private static final String BOARD_DIR = "BulletinBoard/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    @PostMapping("/post")
    @Operation(summary = "자유게시판 게시글 작성", description = "자유게시판에서 게시글을 작성합니다.")
    public ResponseEntity<?> postBoardPost(
        @RequestParam String boardId,
        @RequestPart("userBoardPost") BoardPost userBoardPost,
        @RequestPart("file") MultipartFile file
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);
    
                if(user.isPresent()){

                    if(file != null && !"no-file".equals(file.getOriginalFilename())){

                        // 파일 크기 확인 
                        if(file.getSize() > MAX_FILE_SIZE){
                            return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                        }
        
                        // 파일 정보 저장 
                        Map<String, Object> result = saveFile(file, userBoardPost);
                        Path path = (Path) result.get("path");
                        String uniqueFileName = (String) result.get("uniqueFileName");

                        // 파일의 마지막 경로 (파일명 + 확장자 전까지 저장)
                        String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\")+1);
                        // 고유한 파일 번호 생성 
                        String fileNo = UUID.randomUUID().toString();
                        FileInfo fileInfo = FileInfo.builder()
                        .fileNo(fileNo)
                        .fileSize(Long.toString(file.getSize()))
                        .filePath(filePath)
                        .orgFileNm(file.getOriginalFilename())
                        .encFileNm(uniqueFileName)
                        .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                        .uploaderSessionId(sessionId)
                        .build();
                        freeBulletinBoardPostFileInfoRepository.save(fileInfo);

                        userBoardPost.setFileNo(fileNo);

                    }else{
                        userBoardPost.setFileNo(null);
                    }
                    
                    // 작성자의 닉네임 확인 
                    Optional<FreeBulletinBoardPostUser> findUserNickName = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);
        
                    userBoardPost.setAuthorNickname(findUserNickName.get().getNickname());
                    // PostId는 UUID로 자동 할당받기 때문에 null로 삽입 
                    userBoardPost.setPostId(null);
                    userBoardPost.setSessionId(sessionId);
                    userBoardPost.setBoardId(boardId);
                    userBoardPost.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                    // 공지사항이면 T 아니면 F 
                    if(userBoardPost.getIsNotice().equals("true")){
                        userBoardPost.setIsNotice("T");
                    }else{
                        userBoardPost.setIsNotice("F");
                    }
        
                    BoardPost savePost = freeBulletinBoardPostRepository.save(userBoardPost);
                
                    return ResponseEntity.ok().body(savePost);
    
                }
    
                return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 업로드 실패" + e.getMessage());
            }


    }

    private Map<String, Object> saveFile(MultipartFile file, BoardPost userBoardPost) throws Exception{

        // 원본 파일명에서 확장자 추출 
        String originalFilename = file.getOriginalFilename();
        String fileExtension = ""; // . 부터 시작하는 확장자를 담는 변수

        if(originalFilename != null){
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 고유 파일명 생성 
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + BOARD_DIR + uniqueFileName);

        // 파일이 존재하지 않으면 생성 
        Files.createDirectories(path.getParent());

        // 파일 저장 
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);

        return result;
    }

    // 이미지 반환 
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = freeBulletinBoardPostFileInfoRepository.findByFileNo(fileNo);
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

    // 파일 다운로드 반환 
    @GetMapping("/download/{fileNo}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileNo) {
        try {
            // 파일 정보 조회
            Optional<FileInfo> fileInfoOptional = freeBulletinBoardPostFileInfoRepository.findByFileNo(fileNo);
            
            if (fileInfoOptional.isPresent()) {
                FileInfo fileInfo = fileInfoOptional.get();
                Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {

                    // 파일을 다운로드할 수 있도록 ResponseEntity에 설정
                    String encodedFileName = URLEncoder.encode(fileInfo.getOrgFileNm(), StandardCharsets.UTF_8.toString());
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                            .contentLength(Files.size(filePath)) // 파일 크기 설정
                            .body(resource);
                            
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/comment")
    @Operation(summary = "자유게시판 게시글의 댓글 작성", description = "자유게시판의 게시글에서 댓글을 작성합니다.")
    public ResponseEntity<?> postComment(
        @RequestParam String postId,
        @RequestBody Comment userComment
    ) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

        Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

        if(user.isPresent()){

            // 작석자의 닉네임 확인 
            Optional<FreeBulletinBoardPostUser> findUserNickname = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);
    
            userComment.setAuthorNickname(findUserNickname.get().getNickname());
            userComment.setCommentId(null);
            userComment.setPostId(postId);
            userComment.setSessionId(sessionId);
            userComment.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            // 비밀글인지 확인 
            if(userComment.getIsSecret().equals("true")){
                userComment.setIsSecret("T");
            }else{
                userComment.setIsSecret("F");
            }
            userComment.setPreviousCommentId(null);
            
            Comment saveComment = freeBulletinBoardPostCommentRepository.save(userComment);
            
            userComment.setRootCommentId(userComment.getCommentId());
            freeBulletinBoardPostCommentRepository.save(saveComment);
    
            return ResponseEntity.ok().body(saveComment);

        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");

    }

    @PostMapping("/comment/reply")
    @Operation(summary = "자유게시판 게시글 댓글의 대댓글 작성", description = "자유게시판의 게시글 댓글의 대댓글을 작성합니다.")
    public ResponseEntity<?> postCommentReply(
        @RequestParam String postId,
        @RequestParam String commentId,
        @RequestBody Comment userCommentReply
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            if(user.isPresent()){

                // 작성자의 닉네임 확인 
                Optional<FreeBulletinBoardPostUser> findUserNickname = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);
    
                userCommentReply.setAuthorNickname(findUserNickname.get().getNickname());
                userCommentReply.setCommentId(null);
                userCommentReply.setSessionId(sessionId);
                userCommentReply.setPostId(postId);
                userCommentReply.setRootCommentId(commentId);
                userCommentReply.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                // 비밀글인지 확인 
                if(userCommentReply.getIsSecret().equals("true")){
                    userCommentReply.setIsSecret("T");
                }else{
                    userCommentReply.setIsSecret("F");
                }
    
                // 해당 댓글의 목록 가져오기 
                List<Comment> commentReplys = freeBulletinBoardPostCommentRepository.findByRootCommentId(commentId);
    
                // 작성한 댓글의 이전 댓글의 고유 코드를 찾기 
                Long createdDate = 0L;
                String previousId = null;
                for(Comment commentReply : commentReplys){
                    String commentCreatedDate = commentReply.getCreatedDate();
                    if(createdDate < Long.parseLong(commentCreatedDate)){
                        createdDate = Long.parseLong(commentCreatedDate);
                        previousId = commentReply.getCommentId();
                    }
                }
                userCommentReply.setPreviousCommentId(previousId);
    
                Comment saveCommentReply = freeBulletinBoardPostCommentRepository.save(userCommentReply);
    
            return ResponseEntity.ok().body(saveCommentReply);

            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");

    }
    
    @GetMapping()
    @Operation(summary = "자유게시판 게시글 및 댓글 확인", description = "자유게시판의 게시글과 댓글 목록을 가져옵니다.")
    public ResponseEntity<?> getboardPost(
        @RequestParam String postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {
        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            Optional<FreeBulletinBoardPostPermissionGroup> permissionName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(user.get().getPermissionGroupUuid2());

            String permission = permissionName.get().getPermissionName();
    
            if (user.isPresent()) {
                // 해당 게시글 내용 확인 
                Optional<FreeBulletinBoardPosts> boardPost = freeBulletinBoardPostRepository.findByPostId(postId);
    
                if (!boardPost.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
                }
    
                Map<String, Object> resuList = new HashMap<>();
                HashMap<String, Object> posts = new HashMap<>();
                posts.put("postSessionId", boardPost.get().getSessionId());
                posts.put("postId", boardPost.get().getPostId());
                posts.put("postAuthorNickname", boardPost.get().getAuthorNickname());
                posts.put("postCreatedDate", boardPost.get().getCreatedDate());
                posts.put("postTitle", boardPost.get().getTitle());
                posts.put("postContent", boardPost.get().getContent());
    
                // 파일 정보 추가 
                Optional<FileInfo> fileinfoOptional = freeBulletinBoardPostFileInfoRepository.findByFileNo(boardPost.get().getFileNo());
                if (fileinfoOptional.isPresent()) {
                    FileInfo fileInfo = fileinfoOptional.get();
                    posts.put("fileNo", fileInfo.getFileNo());
                    posts.put("orgFileNm", fileInfo.getOrgFileNm());
                    // 이미지 표시 URL 생성
                    String orgFileNm = fileInfo.getOrgFileNm();
                    if (orgFileNm != null && (orgFileNm.endsWith(".jpg") || orgFileNm.endsWith(".jpeg") || orgFileNm.endsWith(".png"))) {
                        String imageUrl = fileInfo.getFileNo(); // 이미지 URL
                        posts.put("imageUrl", imageUrl); // 이미지 URL 추가
                    }else{
                        // 파일 다운로드 URL 생성
                        String fileDownloadUrl = fileInfo.getFileNo(); // 다운로드 API URL
                        posts.put("fileDownloadUrl", fileDownloadUrl); // 다운로드 링크 추가
                    }
                }
    
                resuList.put("post", posts);
    
                // 해당 게시글의 부모댓글들 확인 
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
                Page<Comment> comments = freeBulletinBoardPostCommentRepository.findByPostIdAndPreviousCommentId(postId, null, pageable);
    
                List<Map<String, Object>> userComments = new ArrayList<>();
                for (Comment comment : comments) {
                    HashMap<String, Object> userComment = new HashMap<>();
                    userComment.put("commentId", comment.getCommentId());
                    userComment.put("commentAuthorNickname", comment.getAuthorNickname());
                    userComment.put("commentCreatedDate", comment.getCreatedDate());
                    if((!comment.getSessionId().equals(sessionId) && comment.getIsSecret().equals("T")) || !permission.equals("OFFICER") || !permission.equals("SITE_OFFICER")){
                        userComment.put("comment", "비밀 댓글 입니다.");
                    }else{
                        userComment.put("comment", comment.getContent());
                    }
                    userComment.put("commentSessionId", comment.getSessionId());
                    userComment.put("isSecret", comment.getIsSecret());
                    userComments.add(userComment);
                }
    
                resuList.put("comments", userComments);
    
                Map<String, Object> response = new HashMap<>();
                response.put("postAndComment", resuList);
                response.put("currentPage", comments.getNumber());
                response.put("totalItems", comments.getTotalElements());
                response.put("totalPages", comments.getTotalPages());
    
                return ResponseEntity.ok().body(response);
    
            }
    
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접속 가능");
    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생: " + e.getMessage());
        }
    }
    

    @GetMapping("/reply")
    @Operation(summary = "자유게시판 댓글의 대댓글 확인", description = "자유게시판의 대댓글 목록을 가져옵니다.")
    public ResponseEntity<?> getCommentReply(
        @RequestParam String commentId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "6") int size
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            // 사용자 확인 
            Optional<UserOwnPermissionGroup> user = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            Optional<FreeBulletinBoardPostPermissionGroup> permissionName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(user.get().getPermissionGroupUuid2());

            String permission = permissionName.get().getPermissionName();

            if(user.isPresent()){

                // 해당 댓글의 대댓글 목록 확인 
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
                Page<Comment> replyComments = freeBulletinBoardPostCommentRepository.findByRootCommentId(commentId, pageable);
    
                List<Map<String, Object>> resultList = new ArrayList<>();
    
                for(Comment replyComment : replyComments) {
    
                    if(replyComment.getPreviousCommentId() != null){
    
    
                        HashMap<String, Object> comment = new HashMap<>();
                        comment.put("replyCommentId", replyComment.getCommentId());
                        comment.put("replyAuthorNickname", replyComment.getAuthorNickname());
                        comment.put("replyCreatedDate", replyComment.getCreatedDate());
                        if((!replyComment.getSessionId().equals(sessionId) && replyComment.getIsSecret().equals("T")) || !permission.equals("OFFICER") || !permission.equals("SITE_OFFICER")){
                            comment.put("replyContent", "비밀 답글 입니다.");
                        }else{
                            comment.put("replyContent", replyComment.getContent());
                        }
                        comment.put("replySessionId", replyComment.getSessionId());
                        comment.put("replyIsSecret", replyComment.getIsSecret());
    
                        resultList.add(comment);
    
                    }
                    
                }

            Map<String, Object> response = new HashMap<>();
            response.put("replyComment", resultList);
            response.put("currentPage", replyComments.getNumber());
            response.put("totalItems", replyComments.getTotalElements());
            response.put("totalPages", replyComments.getTotalPages());
    
            return ResponseEntity.ok().body(response);

            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접속 가능");

    }

    @PostMapping("/postDelete")
    @Operation(summary = "자유게시판 게시글 삭제", description = "자유게시판의 게시글을 삭제합니다")
    public ResponseEntity<?> deletePost(
        @RequestParam String postId
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            // 삭제하려는 게시글의 사용자 세션아이디 확인 
            Optional<FreeBulletinBoardPosts> findUser = freeBulletinBoardPostRepository.findByPostId(postId);

            // 삭제하려는 사용자의 권한 확인 
            Optional<UserOwnPermissionGroup> findOfficer = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            // 권한 이름이 OFFICER 또는 SITE_OFFICER인지 확인
            Optional<FreeBulletinBoardPostPermissionGroup> findOfficerName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(findOfficer.get().getPermissionGroupUuid2());

            String permissionName = findOfficerName.get().getPermissionName();

            if(findUser.get().getSessionId().equals(sessionId) || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER")){

                // 게시글 삭제시 게시글에 포함된 댓글들도 전부 삭제하기 위함 
                List<Comment> deleteComments = freeBulletinBoardPostCommentRepository.findByPostId(postId);
                if(!deleteComments.isEmpty()){
                    for(Comment deleteComment : deleteComments){
                        freeBulletinBoardPostCommentRepository.deleteById(deleteComment.getCommentId());
                    }
                }

                 // 파일 삭제
                String fileNo = findUser.get().getFileNo();
                if (fileNo != null) {
                    Optional<FileInfo> fileInfoOptional = freeBulletinBoardPostFileInfoRepository.findByFileNo(fileNo);
                    if (fileInfoOptional.isPresent()) {
                        FileInfo fileInfo = fileInfoOptional.get();
                        Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
                        try {
                             // 파일 시스템에서 파일 삭제
                            Files.deleteIfExists(filePath);
                        } catch (IOException e) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 삭제 실패: " + e.getMessage());
                        }
                    }
                    // 파일 정보 삭제
                    freeBulletinBoardPostFileInfoRepository.deleteById(fileNo); 
                }

                freeBulletinBoardPostRepository.deleteById(postId);
                return ResponseEntity.ok().body("삭제 완료");

            }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
    }

    @PostMapping("/postComment")
    @Operation(summary = "자유게시판 게시글의 댓글 삭제", description = "자유게시판의 게시글의 댓글을 삭제합니다")
    public ResponseEntity<?> deleteComment(
        @RequestParam String commentId
        ) {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();
            
            // 삭제하려는 댓글의 사용자 세션 아이디 확인 
            Optional<Comment> findUser = freeBulletinBoardPostCommentRepository.findByCommentId(commentId);

            // 삭제하려는 사용자의 권한 확인 
            Optional<UserOwnPermissionGroup> findOfficer = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            // 권한 이름이 OFFICER 또는 SITE_OFFICER인지 확인
            Optional<FreeBulletinBoardPostPermissionGroup> findOfficerName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(findOfficer.get().getPermissionGroupUuid2());

            String permissionName = findOfficerName.get().getPermissionName();

            if(findUser.get().getSessionId().equals(sessionId) || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER")){

                freeBulletinBoardPostCommentRepository.deleteById(commentId);
                return ResponseEntity.ok().body("삭제 완료");

            }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
    }

    @PostMapping("/postUpdate")
    @Operation(summary = "자유게시판 게시글 수정", description = "자유게시판을 수정합니다")
    public ResponseEntity<?> UpdateBullentinBoard(
        @RequestParam String postId,
        @RequestPart("file") MultipartFile file,
        @RequestPart("bullentinUpdatePost") BoardPost bullentinUpdatePost
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();
                
                Optional<FreeBulletinBoardPosts> findPostSessionId = freeBulletinBoardPostRepository.findByPostId(postId);
                
                Optional<UserOwnPermissionGroup> findUser = freeBulletinBoardPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

                if(findUser.isEmpty()){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("잘못된 접근입니다.");
                }
                
                Optional<FreeBulletinBoardPostPermissionGroup> findUserPermissionName = freeBulletinBoardPostPermissionGroupRepository.findByPermissionGroupUuid(findUser.get().getPermissionGroupUuid2());
                
                String permissionName = findUserPermissionName.get().getPermissionName();
                
                if(findPostSessionId.get().equals(sessionId) || permissionName.equals("OFFICER") || permissionName.equals("SITE_OFFICER")){

                    Optional<FreeBulletinBoardPostUser> findUserNickName = freeBulletinBoardPostUserRepository.findBySessionId(sessionId);

                    Optional<BoardPost> updatePost = freeBulletinBoardPostRepository.findById(postId);

                    if(updatePost.isPresent()){

                        BoardPost postToUpdate = updatePost.get();
                        postToUpdate.setAuthorNickname(findUserNickName.get().getNickname());
                        postToUpdate.setSessionId(sessionId);
                        postToUpdate.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                        if(bullentinUpdatePost.getIsNotice().equals("true")){
                            postToUpdate.setIsNotice("T");
                        }else{
                            postToUpdate.setIsNotice("F");
                        }
                        postToUpdate.setTitle(bullentinUpdatePost.getTitle());
                        postToUpdate.setContent(bullentinUpdatePost.getContent());

                        // 파일 업데이트 로직 

                        // 파일이 새로 업로드된 경우 처리 로직 
                        if(file != null && !file.isEmpty() && !"no-file".equals(file.getOriginalFilename())){

                            // 파일 크기 확인 
                            if(file.getSize() > MAX_FILE_SIZE){
                                return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                            }

                            if(postToUpdate.getFileNo() != null){
                                Optional<FileInfo> fileInfoOptional = freeBulletinBoardPostFileInfoRepository.findByFileNo(postToUpdate.getFileNo());
                                if (fileInfoOptional.isPresent()) {
                                    FileInfo fileInfo = fileInfoOptional.get();
                                    Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
                                    
                                    // 파일 삭제
                                    try {
                                        Files.deleteIfExists(filePath);
                                    } catch (IOException e) {
                                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 삭제 실패: " + e.getMessage());
                                    }

                                    // 파일 정보 삭제
                                    freeBulletinBoardPostFileInfoRepository.deleteById(postToUpdate.getFileNo());
                                }
                            }

                            // 파일 저장 및 정보 업데이트 
                            Map<String, Object> result = saveFile(file, postToUpdate);
                            Path path = (Path) result.get("path");
                            String uniqueFileName = (String) result.get("uniqueFileName");

                            // 파일의 마지막 경로 (파일명 + 확장자 전까지 저장)
                            String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\")+1);
                            // 고유한 파일 번호 생성 
                            String fileNo = UUID.randomUUID().toString();
                            FileInfo fileInfo = FileInfo.builder()
                            .fileNo(fileNo)
                            .fileSize(Long.toString(file.getSize()))
                            .filePath(filePath)
                            .orgFileNm(file.getOriginalFilename())
                            .encFileNm(uniqueFileName)
                            .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                            .uploaderSessionId(sessionId)
                            .build();
                            freeBulletinBoardPostFileInfoRepository.save(fileInfo);

                            postToUpdate.setFileNo(fileNo);

                        }else{
                            postToUpdate.setFileNo(postToUpdate.getFileNo());
                        }

                        freeBulletinBoardPostRepository.save(postToUpdate);

                        return ResponseEntity.ok().body("수정 완료");

                    }

                    return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 게시물이 없습니다.");
                    
                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
        
    }
    

}
