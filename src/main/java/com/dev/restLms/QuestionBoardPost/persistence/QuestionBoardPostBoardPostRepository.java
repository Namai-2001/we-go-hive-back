package com.dev.restLms.QuestionBoardPost.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostBoardPost;
// import java.util.List;
import java.util.Optional;



@Repository
public interface QuestionBoardPostBoardPostRepository extends JpaRepository<QuestionBoardPostBoardPost, String> {
    Optional<QuestionBoardPostBoardPost> findBySessionId(String sessionId);

    Optional<QuestionBoardPostBoardPost> findByPostId(String postId);

    Optional<QuestionBoardPostBoardPost> findBySessionIdAndPostId(String sessionId, String postId);

}
