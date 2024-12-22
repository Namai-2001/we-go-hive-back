package com.dev.restLms.juhwi.MessageService.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.MSG_M_Self_Projection;


public interface MSG_Self_Repository extends JpaRepository<Message, String>{
    // 내게 쓴 쪽지 조회
    Page<MSG_M_Self_Projection> findBySenderDeleteAndSenderSessionIdAndReceiverSessionId(String senderDelete, String senderSessionId, String receiversessionId, Pageable pageable);
    Page<MSG_M_Self_Projection> findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdAndMessageTitleContaining(String senderDelete, String senderSessionId, String receiversessionId, String messageTitle, Pageable pageable);
}
