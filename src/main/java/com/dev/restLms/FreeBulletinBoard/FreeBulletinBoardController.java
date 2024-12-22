package com.dev.restLms.FreeBulletinBoard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("freeBulletinBoard")
@Tag(name = "FreeBulletinBoardController", description = "자유 게시판 목록")
public class FreeBulletinBoardController {

    @Autowired
    FreeBulletinBoardRepository freeBulletinBoardRepository;

    @Autowired
    FreeBulletinBoardPostsRepository freeBulletinBoardPostRepository;

    @GetMapping()
    @Operation(summary = "자유 게시판의 게시글", description = "자유 게시판의 게시글들을 반환합니다.")
    public ResponseEntity<?> getBoardPost(
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        // 자유 게시판의 게시판 아이디 확인 
        Optional<FreeBulletinBoard> findBoardId = freeBulletinBoardRepository.findByBoardCategory("자유게시판");

        if(findBoardId.isPresent()){
            
            // 자유게시판의 게시글 확인 
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
            Page<FreeBulletinBoardPost> findBoardPosts = freeBulletinBoardPostRepository.findByBoardId(findBoardId.get().getBoardId(), pageable);
    
            List<Map<String, Object>> resultList = new ArrayList<>();
    
            for(FreeBulletinBoardPost findBoardPost : findBoardPosts){
    
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
