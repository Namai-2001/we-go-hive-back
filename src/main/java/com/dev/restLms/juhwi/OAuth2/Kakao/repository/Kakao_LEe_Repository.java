package com.dev.restLms.juhwi.OAuth2.Kakao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.LoginEmails;
import java.util.List;


public interface Kakao_LEe_Repository extends JpaRepository<LoginEmails, String>{
    List<LoginEmails> findByEmail(String email);
}
