package com.dev.restLms.announcement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.BoardPost;
import java.util.List;
import java.util.Optional;




public interface AnnouncementBoardPostRepository extends JpaRepository<BoardPost, String> {

    // Page<announcementBoardPost> findByBoardId(String boardId, Pageable pageable);  

    Page<announcementBoardPost> findByBoardIdAndIsNotice(String boardId, String isNotice, Pageable pageable);

    List<announcementBoardPost> findByBoardId(String boardId, Sort sort);

    Optional<announcementBoardPost> findByPostId(String postId);
}
