package com.dev.restLms.hyeon.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;

public interface ProfileFileinfoRepository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
    Optional<FileInfo> findByFileNoAndUploaderSessionId(String fileNo, String uploaderSessionId);
}