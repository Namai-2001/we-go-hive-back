package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseComment;
import com.dev.restLms.entity.Comment;
import java.util.List;


public interface DeleteCourseCommentRepository extends JpaRepository<Comment, String> {
    boolean existsByPostId(String postId);
    void deleteByPostId(String postId);
    List<DeleteCourseComment> findByPostId(String postId);
}
