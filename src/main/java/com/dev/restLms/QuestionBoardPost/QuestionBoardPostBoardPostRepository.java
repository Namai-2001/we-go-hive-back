package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.BoardPost;

// import java.util.List;
import java.util.Optional;



@Repository
public interface QuestionBoardPostBoardPostRepository extends JpaRepository<BoardPost, String> {
    Optional<QuestionBoardPostBoardPost> findBySessionId(String sessionId);

    Optional<QuestionBoardPostBoardPost> findByPostId(String postId);

    Optional<QuestionBoardPostBoardPost> findBySessionIdAndPostId(String sessionId, String postId);

}
