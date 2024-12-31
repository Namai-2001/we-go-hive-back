package com.dev.restLms.sechan.subjectInfoDetailPage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;

public interface SID_F_Repository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
