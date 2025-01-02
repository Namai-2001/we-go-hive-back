package com.dev.restLms.juhwi.OAuth2.Naver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.LoginEmails;
import java.util.List;


public interface Naver_LEe_Repository extends JpaRepository<LoginEmails,String>{
    List<LoginEmails> findByEmail(String email);
}
