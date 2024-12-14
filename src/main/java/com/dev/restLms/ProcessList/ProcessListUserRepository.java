package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.User;

// import java.util.List;
import java.util.Optional;


@Repository
public interface ProcessListUserRepository extends JpaRepository<User, String> {
    // List<ProcessListUser> findBySessionId(String sessionId);
    Optional<ProcessListUser> findBySessionId(String sessionId);
}
