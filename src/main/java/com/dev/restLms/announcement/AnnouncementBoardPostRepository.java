package com.dev.restLms.announcement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.BoardPost;


public interface AnnouncementBoardPostRepository extends JpaRepository<BoardPost, String> {
    Page<announcementBoardPost> findByBoardId(String boardId, Pageable pageable);  
}
