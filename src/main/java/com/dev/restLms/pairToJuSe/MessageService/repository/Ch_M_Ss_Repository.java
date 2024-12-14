package com.dev.restLms.pairToJuSe.MessageService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.pairToJuSe.MessageService.projection.Ch_M_Ss_Projcction;

public interface Ch_M_Ss_Repository extends JpaRepository<Message, String> {
    Page<Ch_M_Ss_Projcction> findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdNot(String senderDelete,
            String senderSessionId1, String senderSessionId2, Pageable pageable);
}
