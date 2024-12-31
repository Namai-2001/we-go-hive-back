package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
import java.util.Optional;


public interface FreeBulletinBoardPostFileInfoRepository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
