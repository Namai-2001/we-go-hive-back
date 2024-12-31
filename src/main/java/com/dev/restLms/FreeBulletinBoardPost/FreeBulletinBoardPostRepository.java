package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.BoardPost;
import java.util.Optional;


public interface FreeBulletinBoardPostRepository extends JpaRepository<BoardPost, String> {
    Optional<FreeBulletinBoardPosts> findByPostId(String postId);
}
