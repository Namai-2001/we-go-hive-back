package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;

public interface TSR_File_Repository extends JpaRepository<FileInfo, String> {
    
}
