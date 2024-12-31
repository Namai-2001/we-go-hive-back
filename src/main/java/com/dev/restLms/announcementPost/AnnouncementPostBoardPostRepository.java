package com.dev.restLms.announcementPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.BoardPost;
import java.util.Optional;


public interface AnnouncementPostBoardPostRepository extends JpaRepository<BoardPost, String> {
    Optional<announcementPostBoardPost> findByPostId(String postId);
}
