package com.dev.restLms.juhwi.MessageService.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Inbox_Projection;

public interface MSG_Inbox_Repository extends JpaRepository<Message, String>{
    Page<MSG_Inbox_Projection> findByReceiverDeleteAndReceiverSessionIdAndSenderSessionIdNot(String receiverDelete, String receiverSessionId, String senderSessionIdNot,Pageable pageable);
    Page<MSG_Inbox_Projection> findByReceiverDeleteAndReceiverSessionIdAndSenderSessionIdNotAndMessageTitleContaining(String receiverDelete, String receiverSessionId, String senderSessionIdNot, String messageTitle,Pageable pageable);
}
