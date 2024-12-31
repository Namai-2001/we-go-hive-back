package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseComment;
import com.dev.restLms.entity.Comment;
import java.util.List;


public interface ModifyCourseCommentRepository extends JpaRepository<Comment, String> {
    List<ModifyCourseComment> findByPostId(String postId);
}
