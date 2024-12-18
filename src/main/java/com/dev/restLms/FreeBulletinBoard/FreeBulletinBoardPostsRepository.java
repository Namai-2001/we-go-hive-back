package com.dev.restLms.FreeBulletinBoard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.BoardPost;


public interface FreeBulletinBoardPostsRepository extends JpaRepository<BoardPost, String> {
    Page<FreeBulletinBoardPost> findByBoardId(String boardId, Pageable pageable);
}
