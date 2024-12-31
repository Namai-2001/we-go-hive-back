package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;

public interface TV_File_Repository extends JpaRepository<FileInfo, String>{
    Optional<FileInfo> findByFileNo(String fileNo);
}
