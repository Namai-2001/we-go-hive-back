package com.dev.restLms.announcement;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/announcement")
@Tag(name = "announcementController", description = "공지사항 게시판")
public class announcementController {

    @Autowired
    AnnouncementBoardRepository announcementBoardRepository;

    @Autowired
    AnnouncementBoardPostRepository announcementBoardPostRepository;

    @GetMapping()
    @Operation(summary = "공지사항 게시글", description = "공지사항의 게시글들을 반환합니다.")
    public ResponseEntity<?> getAnnountcementPost(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
        ) {

            // 공지사항 아이디 확인 
            Optional<announcementBoard> findBoardId = announcementBoardRepository.findByBoardCategory("공지사항");

            if(findBoardId.isPresent()){

                // 공지사항 게시판 확인
                Pageable pageable = PageRequest.of(page, size);
                Page<announcementBoardPost> findBoardPosts = announcementBoardPostRepository.findByBoardId(findBoardId.get().getBoardId(), pageable);

                List<Map<String, Object>> resultList = new ArrayList<>();

                for(announcementBoardPost findBoardPost : findBoardPosts){

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("postId", findBoardPost.getPostId());
                    postMap.put("title", findBoardPost.getTitle());
                    postMap.put("authorNickname", findBoardPost.getAuthorNickname());
                    postMap.put("createdDate", findBoardPost.getCreatedDate());
                    postMap.put("isNotice", findBoardPost.getIsNotice());
                    postMap.put("boardId", findBoardId.get().getBoardId());
                    postMap.put("boardCategory", findBoardId.get().getBoardCategory());
                    resultList.add(postMap);

                }

                Map<String, Object> response = new HashMap<>();
                response.put("posts", resultList);
                response.put("currentPage", findBoardPosts.getNumber());
                response.put("totalItems", findBoardPosts.getTotalElements());
                response.put("totalPages", findBoardPosts.getTotalPages());

                return ResponseEntity.ok().body(response);

            }


        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 게시판이 존재하지 않습니다.");
    }
    
    
    
}
