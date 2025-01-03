package com.dev.restLms.FreeBulletinBoard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.BoardPost;
import java.util.List;
import java.util.Optional;



public interface FreeBulletinBoardPostsRepository extends JpaRepository<BoardPost, String> {
    // Page<FreeBulletinBoardPost> findByBoardId(String boardId, Pageable pageable);

    List<FreeBulletinBoardPost> findByBoardId(String boardId, Sort sort);

    Optional<FreeBulletinBoardPost> findByPostId(String postId);
}
