package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dev.restLms.entity.CourseOwnSubject;
//import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.S_Projection;
import java.util.List;

public interface COS_Repository extends JpaRepository<CourseOwnSubject, String> {
    List<CourseOwnSubject> findByCourseId(String courseId);
}