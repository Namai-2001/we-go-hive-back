package com.dev.restLms.QuestionBoardPost.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoardPost.model.QuestionBoardPostComment;
// import java.util.List;
import java.util.Optional;


@Repository
public interface QuestionBoardPostCommentRepository extends JpaRepository<QuestionBoardPostComment, String> {
    Optional<QuestionBoardPostComment> findByPostId(String postId);
}
