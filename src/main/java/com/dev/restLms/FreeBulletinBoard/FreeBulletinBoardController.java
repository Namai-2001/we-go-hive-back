package com.dev.restLms.FreeBulletinBoard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.announcement.announcementBoardPost;
import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.entity.UserOwnPermissionGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("freeBulletinBoard")
@Tag(name = "FreeBulletinBoardController", description = "자유 게시판 목록")
public class FreeBulletinBoardController {

    @Autowired
    FreeBulletinBoardRepository freeBulletinBoardRepository;

    @Autowired
    FreeBulletinBoardPostsRepository freeBulletinBoardPostRepository;

    @Autowired
    FreeBulletinBoardUserOwnPermissionGroupRepository freeBulletinBoardUserOwnPermissionGroupRepository;
    
    @Autowired
    FreeBulletinBoardPermissionGroupRepository freeBulletinBoardPermissionGroupRepository;

    @PostMapping()
    @Operation(summary = "자유 게시판의 게시글", description = "자유 게시판의 게시글들을 반환합니다.")
    public ResponseEntity<?> searchFreeBulletinBoard (
        @RequestParam String title,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue = "10") int size
        ) {

            try {
                 // 자유 게시판의 게시판 아이디 확인 
                Optional<FreeBulletinBoard> findBoardId = freeBulletinBoardRepository.findByBoardCategory("자유게시판");

                List<Map<String, Object>> resultList = new ArrayList<>();

                if(findBoardId.isPresent()){

                    List<FreeBulletinBoardPost> findPostIds = freeBulletinBoardPostRepository.findByBoardId(findBoardId.get().getBoardId(), Sort.by(Sort.Direction.DESC, "createdDate"));
    
                    for(FreeBulletinBoardPost findPostId :  findPostIds ){
    
                        Optional<FreeBulletinBoardPost> findPostTitle = freeBulletinBoardPostRepository.findByPostId(findPostId.getPostId());
    
                        if(findPostTitle.isPresent()){
    
                            if(findPostTitle.get().getTitle().contains(title)){
    
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("postId", findPostId.getPostId());
                                postMap.put("title", findPostId.getTitle());
                                postMap.put("authorNickname", findPostId.getAuthorNickname());
                                postMap.put("createdDate", findPostId.getCreatedDate());
                                postMap.put("isNotice", findPostId.getIsNotice());
                                postMap.put("boardId", findBoardId.get().getBoardId());
                                postMap.put("boardCategory", findBoardId.get().getBoardCategory());
                                resultList.add(postMap);
    
                            }
    
                        }
    
                    }
    
                    // 페이징 처리
                    int totalItems = resultList.size();
                    int totalPages = (int) Math.ceil((double) totalItems / size);
                    int start = page * size;
                    int end = Math.min(start + size, totalItems);

                    List<Map<String, Object>> pagedResultList = resultList.subList(start, end);

                    Map<String, Object> response = new HashMap<>();
                    response.put("posts", pagedResultList);
                    response.put("boardId", findBoardId.get().getBoardId());
                    response.put("currentPage", page);
                    response.put("totalItems", totalItems);
                    response.put("totalPages", totalPages);

                    return ResponseEntity.ok().body(response);
                    
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("게시판이 존재하지 않습니다.");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
        
    }
    

    // @GetMapping()
    // @Operation(summary = "자유 게시판의 게시글", description = "자유 게시판의 게시글들을 반환합니다.")
    // public ResponseEntity<?> getBoardPost(
    //     @RequestParam(defaultValue="0") int page,
    //     @RequestParam(defaultValue = "10") int size
    // ) {

    //     // 자유 게시판의 게시판 아이디 확인 
    //     Optional<FreeBulletinBoard> findBoardId = freeBulletinBoardRepository.findByBoardCategory("자유게시판");

    //     if(findBoardId.isPresent()){
            
    //         // 자유게시판의 게시글 확인 
    //         Sort sort = Sort.by(Sort.Direction.DESC, "isNotice").and(Sort.by(Sort.Direction.DESC, "createdDate"));
    //         Pageable pageable = PageRequest.of(page, size, sort);
    //         Page<FreeBulletinBoardPost> findBoardPosts = freeBulletinBoardPostRepository.findByBoardId(findBoardId.get().getBoardId(), pageable);
    
    //         List<Map<String, Object>> resultList = new ArrayList<>();
    
    //         for(FreeBulletinBoardPost findBoardPost : findBoardPosts){
    
    //             Map<String, Object> postMap = new HashMap<>();
    //             postMap.put("postId", findBoardPost.getPostId());
    //             postMap.put("title", findBoardPost.getTitle());
    //             postMap.put("authorNickname", findBoardPost.getAuthorNickname());
    //             postMap.put("createdDate", findBoardPost.getCreatedDate());
    //             postMap.put("isNotice", findBoardPost.getIsNotice());
    //             postMap.put("boardId", findBoardId.get().getBoardId());
    //             postMap.put("boardCategory", findBoardId.get().getBoardCategory());
    //             resultList.add(postMap);
    
    //         }
            
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("posts", resultList);
    //         response.put("boardId", findBoardId.get().getBoardId());
    //         response.put("currentPage", findBoardPosts.getNumber());
    //         response.put("totalItems", findBoardPosts.getTotalElements());
    //         response.put("totalPages", findBoardPosts.getTotalPages());

    //         return ResponseEntity.ok().body(response);

    //     }

    //     return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 게시판이 존재하지 않습니다.");

    // }
    
}
