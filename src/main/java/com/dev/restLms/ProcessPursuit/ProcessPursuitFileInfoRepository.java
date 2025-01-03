package com.dev.restLms.ProcessPursuit;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
import java.util.Optional;


public interface ProcessPursuitFileInfoRepository extends JpaRepository<FileInfo, String>{
    Optional<FileInfo> findByFileNo(String fileNo);
}
