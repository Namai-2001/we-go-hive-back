package com.dev.restLms.juhwi.MessageService.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.Ch_M_MSR_Projection;


public interface Ch_M_MSR_Repository extends JpaRepository<Message, String>{
    // 내게 쓴 쪽지 조회
    Page<Ch_M_MSR_Projection> findBySenderDeleteAndSenderSessionIdAndReceiverSessionId(String senderDelete, String senderSessionId, String receiversessionId, Pageable pageable);
}
