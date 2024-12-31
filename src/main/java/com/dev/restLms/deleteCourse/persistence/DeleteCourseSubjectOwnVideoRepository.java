package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseSubjectOwnVideo;
import com.dev.restLms.entity.SubjectOwnVideo;
import java.util.List;


public interface DeleteCourseSubjectOwnVideoRepository extends JpaRepository<SubjectOwnVideo, String> {
 List<DeleteCourseSubjectOwnVideo> findBySovOfferedSubjectsId(String sovOfferedSubjectsId);   
}
