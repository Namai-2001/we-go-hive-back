package com.dev.restLms.juhwi.MessageService.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;


// 기본적인 UD를 위한 
public interface MSG_Entity_Repository extends JpaRepository<Message, String>{
}
