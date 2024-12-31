package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseSubjectOwnVideo;
import com.dev.restLms.entity.SubjectOwnVideo;
import java.util.List;


public interface ModifyCourseSubjectOwnVideoRepository extends JpaRepository<SubjectOwnVideo, String> {
    List<ModifyCourseSubjectOwnVideo> findBySovOfferedSubjectsId(String sovOfferedSubjectsId);
}
