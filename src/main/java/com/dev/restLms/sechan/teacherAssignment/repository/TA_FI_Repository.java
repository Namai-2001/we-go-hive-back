package com.dev.restLms.sechan.teacherAssignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;

public interface TA_FI_Repository extends JpaRepository<FileInfo, String> {
    FileInfo findByFileNo(String fileNo);    
}
