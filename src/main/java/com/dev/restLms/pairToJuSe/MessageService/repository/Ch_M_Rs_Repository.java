package com.dev.restLms.pairToJuSe.MessageService.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Message;
import com.dev.restLms.pairToJuSe.MessageService.projection.Ch_M_Rs_Projection;

public interface Ch_M_Rs_Repository extends JpaRepository<Message, String>{
    Page<Ch_M_Rs_Projection> findByReceiverDeleteAndReceiverSessionIdAndSenderSessionIdNot(String receiverDelete, String receiverSessionId, String senderSessionIdNot,Pageable pageable);
}
