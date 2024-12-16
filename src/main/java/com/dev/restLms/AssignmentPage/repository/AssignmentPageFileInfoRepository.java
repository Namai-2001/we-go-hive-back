package com.dev.restLms.AssignmentPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.FileInfo;

@Repository
public interface AssignmentPageFileInfoRepository extends JpaRepository<FileInfo, String>{
  
}
