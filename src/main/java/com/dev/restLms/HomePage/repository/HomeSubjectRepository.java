package com.dev.restLms.HomePage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;

public interface HomeSubjectRepository extends JpaRepository<Subject, String>{
  
}
