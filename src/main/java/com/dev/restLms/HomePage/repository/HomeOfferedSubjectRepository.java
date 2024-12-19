package com.dev.restLms.HomePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.HomePage.projection.HomeOfferedSubjectsProjection;
import com.dev.restLms.entity.OfferedSubjects;

public interface HomeOfferedSubjectRepository extends JpaRepository<OfferedSubjects, String> {
  List<HomeOfferedSubjectsProjection> findByCourseIdIsNull();
}
