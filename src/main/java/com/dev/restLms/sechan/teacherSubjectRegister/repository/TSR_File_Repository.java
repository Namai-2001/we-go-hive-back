package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
// import java.util.List;
import java.util.Optional;


public interface TSR_File_Repository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
