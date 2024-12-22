package com.dev.restLms.juhwi.MessageService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Admin_Projection;

public interface MSG_Admin_Repository extends JpaRepository<Message, String>{
    Page<MSG_Admin_Projection> findBySenderSessionIdAndReceiverSessionId(String senderSessionId, String receiverSessionId, Pageable pageable);
}