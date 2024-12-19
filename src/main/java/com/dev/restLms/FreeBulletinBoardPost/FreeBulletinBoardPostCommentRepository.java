package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import com.dev.restLms.entity.Comment;
import java.util.List;
import java.util.Optional;


public interface FreeBulletinBoardPostCommentRepository extends JpaRepository<Comment, String> {
@RestResource(path = "findByRootCommentIdreply")
List<Comment> findByRootCommentId(String rootCommentId);

@RestResource(path = "findByRootCommentIdPaged")
Page<Comment> findByRootCommentId(String rootCommentId, Pageable pageable);

List<Comment> findByPostId(String postId);
Page<Comment> findByPostIdAndPreviousCommentId(String postId, String previousCommentId, Pageable pageable);
Optional<Comment> findByCommentId(String commentId);
}
