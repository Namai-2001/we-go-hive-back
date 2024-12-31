package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseBookMark;
import com.dev.restLms.entity.BookMark;
import java.util.List;


public interface DeleteCourseBookMarkRepository extends JpaRepository<BookMark, String> {

    List<DeleteCourseBookMark> findByBmOfferedSubjectsId(String bmOfferedSubjectsId);
    
}
