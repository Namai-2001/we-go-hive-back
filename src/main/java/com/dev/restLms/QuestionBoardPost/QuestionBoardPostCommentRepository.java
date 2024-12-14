package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.Comment;

// import java.util.List;
import java.util.Optional;


@Repository
public interface QuestionBoardPostCommentRepository extends JpaRepository<Comment, String> {
    Optional<QuestionBoardPostComment> findByPostId(String postId);
}
