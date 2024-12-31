package com.dev.restLms.announcementPost;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.entity.BoardPost;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.UserOwnPermissionGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/announcementPost")
@Tag(name = "announcementPostController", description = "공지사항")
public class announcementPostController {

    @Autowired
    AnnouncementPostBoardPostRepository announcementPostBoardPostRepository;

    @Autowired
    AnnouncementPostPermissionGroupRepository announcementPostPermissionGroupRepository;

    @Autowired
    AnnouncementPostUserRepository announcementPostUserRepository;

    @Autowired
    AnnouncementPostUserOwnPermissionGroupRepository announcementPostUserOwnPermissionGroupRepository;

    @Autowired
    AnnouncementPostFileInfoRepository announcementPostFileInfoRepository;

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "Board/";
    private static final String BOARD_DIR = "NoticeBoard/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    @PostMapping("/post")
    @Operation(summary = "공지사항 작성", description = "공지사항을 작성합니다.")
    public ResponseEntity<?> postAnnouncement(
        @RequestParam String boardId,
        @RequestPart("officerBoardPost") BoardPost officerBoardPost,
        @RequestPart("file") MultipartFile file
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                 // 회원 확인 
                Optional<UserOwnPermissionGroup> officerSessionIdCheck = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

                if(officerSessionIdCheck.isPresent()){

                    // 권한 확인 
                    Optional<announcementPostPermissionGroup> offcerCheck = announcementPostPermissionGroupRepository.findByPermissionGroupUuid(officerSessionIdCheck.get().getPermissionGroupUuid2());

                    String officerName = offcerCheck.get().getPermissionName();

                    if(officerName.equals("OFFICER") || officerName.equals("SITE_OFFICER")){

                        if(file != null && !"no-file".equals(file.getOriginalFilename())){

                            // 파일 크기 확인 
                            if(file.getSize() > MAX_FILE_SIZE){
                                return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                            }

                            // 파일 정보 저장 
                            Map<String, Object> result = saveFile(file, officerBoardPost);
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
                            announcementPostFileInfoRepository.save(fileInfo);

                            officerBoardPost.setFileNo(fileNo);

                        }else{
                            officerBoardPost.setFileNo(null);
                        }

                        // 관리자 닉네임 확인 
                        Optional<announcementPostUser> findOfficerNickname = announcementPostUserRepository.findBySessionId(sessionId);

                        officerBoardPost.setAuthorNickname(findOfficerNickname.get().getNickname());
                        officerBoardPost.setPostId(null);
                        officerBoardPost.setSessionId(sessionId);
                        officerBoardPost.setBoardId(boardId);
                        officerBoardPost.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                        if(officerBoardPost.getIsNotice().equals("true")){
                            officerBoardPost.setIsNotice("T");
                        }else{
                            officerBoardPost.setIsNotice("F");
                        }

                        BoardPost saveAnnouncementPost = announcementPostBoardPostRepository.save(officerBoardPost);

                        return ResponseEntity.ok().body(saveAnnouncementPost);

                    }

                    return ResponseEntity.status(HttpStatus.CONFLICT).body("작성 권한이 없습니다.");

                }
        
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 업로드 실패" + e.getMessage());
            }

            
    }

    private Map<String, Object> saveFile(MultipartFile file, BoardPost officerBoardPost) throws Exception{

        // 원본 파일명에서 확장자 추출 
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

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

    @PostMapping("/postUpdate")
    @Operation(summary = "공지사항 게시글 수정", description = "공지사항을 수정합니다.")
    public ResponseEntity<?> updateAnnouncement(
        @RequestParam String postId,
        @RequestPart("file") MultipartFile file,
        @RequestPart("officerUpdatePost") BoardPost officerUpdatePost
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                Optional<UserOwnPermissionGroup> officerCheck = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);

                if(officerCheck.isPresent()){

                    Optional<announcementPostPermissionGroup> officerNameCheck = announcementPostPermissionGroupRepository.findByPermissionGroupUuid(officerCheck.get().getPermissionGroupUuid2());

                    String officerName = officerNameCheck.get().getPermissionName();

                    if(officerName.equals("OFFICER") || officerName.equals("SITE_OFFICER")){

                        Optional<announcementPostUser> findOfficerNickname = announcementPostUserRepository.findBySessionId(sessionId);

                        Optional<BoardPost> updatePost = announcementPostBoardPostRepository.findById(postId);
                        
                        if(updatePost.isPresent()){
                            
                            BoardPost postToUpdate = updatePost.get();
                            postToUpdate.setAuthorNickname(findOfficerNickname.get().getNickname());
                            postToUpdate.setSessionId(sessionId);
                            postToUpdate.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                            if(officerUpdatePost.getIsNotice().equals("true")){
                                postToUpdate.setIsNotice("T");
                            }else{
                                postToUpdate.setIsNotice("F");
                            }
                            postToUpdate.setTitle(officerUpdatePost.getTitle());
                            postToUpdate.setContent(officerUpdatePost.getContent());

                            // 파일 업데이터 로직 

                            // 파일이 새로 업로드된 경우 처리 로직 
                            if(file != null && !file.isEmpty() && !"no-file".equals(file.getOriginalFilename())){

                                // 파일 크기 확인 
                                if(file.getSize() > MAX_FILE_SIZE){
                                    return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                                }

                                if(postToUpdate.getFileNo() != null){
                                    Optional<FileInfo> fileInfoOptional = announcementPostFileInfoRepository.findByFileNo(postToUpdate.getFileNo());
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
                                        announcementPostFileInfoRepository.deleteById(postToUpdate.getFileNo());
                                    }
                                }

                                // 파일 저장 및 정보 없데이트 
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
                                announcementPostFileInfoRepository.save(fileInfo);

                                postToUpdate.setFileNo(fileNo);

                            }else{
                                postToUpdate.setFileNo(postToUpdate.getFileNo());
                            }

                            announcementPostBoardPostRepository.save(postToUpdate);

                            return ResponseEntity.ok().body("수정 완료");
                            
                        }
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 게시물이 없습니다.");

                    }

                }
        
            return ResponseEntity.status(HttpStatus.CONFLICT).body("잘못된 접근입니다.");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

            
    }

    @GetMapping()
    @Operation(summary = "공지사항 확인", description = "공지사항 게시글을 가져옵니다.")
    public ResponseEntity<?> getAnnouncement(
        @RequestParam String postId
    ) {
        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

            Optional<UserOwnPermissionGroup> userCheck = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);
    
            if (userCheck.isPresent()) {
                Optional<announcementPostBoardPost> announcementPost = announcementPostBoardPostRepository.findByPostId(postId);
    
                if (!announcementPost.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 공지사항이 없습니다.");
                }
    
                Map<String, Object> post = new HashMap<>();
                post.put("postSessionId", announcementPost.get().getSessionId());
                post.put("postId", announcementPost.get().getPostId());
                post.put("postAuthorNickname", announcementPost.get().getAuthorNickname());
                post.put("postCreatedDate", announcementPost.get().getCreatedDate());
                post.put("postTitle", announcementPost.get().getTitle());
                post.put("postContent", announcementPost.get().getContent());
                post.put("postIsNotice", announcementPost.get().getIsNotice());
    
                Optional<FileInfo> fileinfoOptional = announcementPostFileInfoRepository.findByFileNo(announcementPost.get().getFileNo());
                if (fileinfoOptional.isPresent()) {
                    FileInfo fileInfo = fileinfoOptional.get();
                    post.put("fileNo", fileInfo.getFileNo());
                    post.put("orgFileNm", fileInfo.getOrgFileNm());

                    // 이미지 표시 URL 생성
                    String orgFileNm = fileInfo.getOrgFileNm();
                    if (orgFileNm != null && (orgFileNm.endsWith(".jpg") || orgFileNm.endsWith(".jpeg") || orgFileNm.endsWith(".png"))) {
                        String imageUrl = fileInfo.getFileNo(); // 이미지 URL
                        post.put("imageUrl", imageUrl); // 이미지 URL 추가
                    }else{
                        // 파일 다운로드 URL 생성
                        String fileDownloadUrl = fileInfo.getFileNo(); // 다운로드 API URL
                        post.put("fileDownloadUrl", fileDownloadUrl); // 다운로드 링크 추가
                    }
                    
                }
    
                return ResponseEntity.ok().body(post);
            }
    
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접근 가능");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생: " + e.getMessage());
        }
    }

    // 이미지 반환 
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = announcementPostFileInfoRepository.findByFileNo(fileNo);
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
            Optional<FileInfo> fileInfoOptional = announcementPostFileInfoRepository.findByFileNo(fileNo);
            
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

    @PostMapping("postDelete")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다")
    public ResponseEntity<?> deleteAnnouncement(
        @RequestParam String postId 
    ) {
        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();
            
            // 사용자 권한 확인
            Optional<UserOwnPermissionGroup> officerOptional = announcementPostUserOwnPermissionGroupRepository.findBySessionId(sessionId);
            if (!officerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 접근 가능합니다.");
            }

            String permissionGroupUuid = officerOptional.get().getPermissionGroupUuid2();
            Optional<announcementPostPermissionGroup> officerNameOptional = announcementPostPermissionGroupRepository.findByPermissionGroupUuid(permissionGroupUuid);

            if (officerNameOptional.isPresent()) {
                String officerPermission = officerNameOptional.get().getPermissionName();

                if ("OFFICER".equals(officerPermission) || "SITE_OFFICER".equals(officerPermission)) {
                    // 게시글의 FileNo 찾기 
                    Optional<announcementPostBoardPost> announcementPostOptional = announcementPostBoardPostRepository.findByPostId(postId);
                    
                    if (!announcementPostOptional.isPresent()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 게시글이 존재하지 않습니다.");
                    }

                    // 파일 삭제
                    String fileNo = announcementPostOptional.get().getFileNo();
                    if (fileNo != null) {
                        Optional<FileInfo> fileInfoOptional = announcementPostFileInfoRepository.findByFileNo(fileNo);
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
                            announcementPostFileInfoRepository.deleteById(fileNo);
                        }
                    }

                    // 게시글 삭제
                    announcementPostBoardPostRepository.deleteById(postId);
                    return ResponseEntity.ok().body("삭제 완료");
                }
            }
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생: " + e.getMessage());
        }
    }

    
}
