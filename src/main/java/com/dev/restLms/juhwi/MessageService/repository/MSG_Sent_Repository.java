package com.dev.restLms.juhwi.MessageService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.MSG_M_Sent_Projcction;

public interface MSG_Sent_Repository extends JpaRepository<Message, String> {
    Page<MSG_M_Sent_Projcction> findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdNot(String senderDelete,
            String senderSessionId1, String senderSessionId2, Pageable pageable);

    Page<MSG_M_Sent_Projcction> findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdNotAndMessageTitleContaining(
        String senderDelete, String senderSessionId1, String senderSessionId2, String messageTittle, Pageable pageable
    );
}
