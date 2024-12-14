package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    private String messageId;

    private String messageTitle;

    private String messageContent;

    private String senderSessionId;

    private String receiverSessionId;

    private String senderDelete;

    private String receiverDelete;

    private String sendTime;

    private String receiveTime;

    private String receiverCheck;

    @PrePersist
    public void generateUUID() {
        if (messageId == null) {
            messageId = UUID.randomUUID().toString();
        }
    }
}
