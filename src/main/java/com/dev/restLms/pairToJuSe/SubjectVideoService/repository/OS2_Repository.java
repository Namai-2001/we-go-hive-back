package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.List;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;
//import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.U_Projection;

public interface OS2_Repository extends JpaRepository<OfferedSubjects, String>{
    List<OfferedSubjects> findByCourseId(String courseId);
}
