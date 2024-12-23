package com.dev.restLms.juhwi.MessageService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Details_Projection;



public interface MSG_Details_Repository extends JpaRepository<Message, String>{
    Optional<MSG_Details_Projection> findByMessageId(String messageId);
}
