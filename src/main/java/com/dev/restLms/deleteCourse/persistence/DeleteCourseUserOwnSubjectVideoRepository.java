package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseUserOwnSubjectVideo;
import com.dev.restLms.entity.UserOwnSubjectVideo;
import java.util.List;


public interface DeleteCourseUserOwnSubjectVideoRepository extends JpaRepository<UserOwnSubjectVideo, String> {
    void deleteByUosvOfferedSubjectsId(String offeredSubjectsId);

    boolean existsByUosvOfferedSubjectsId(String uosvOfferedSubjectsId);

    List<DeleteCourseUserOwnSubjectVideo> findByUosvOfferedSubjectsId(String uosvOfferedSubjectsId);
}
