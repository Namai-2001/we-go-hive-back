package com.dev.restLms.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @Column(name = "MESSAGE_ID")
    private String messageId; // MESSAGE_ID

    @Column(name = "MESSAGE_CONTENT")
    private String messageContent; // MESSAGE_CONTENT



    
    @Column(name = "SENDER_SESSION_ID")
    private String senderSessionId; // SENDER_SESSION_ID


    @Column(name = "RECEIVER_SESSION_ID")
    private String receiverSessionId; // RECEIVER_SESSION_ID
    
    @Column(name = "SENDER_DELETE")
    private char senderDelete; // SENDER_DELETE
    
    @Column(name = "RECEIVER_DELETE")
    private char receiverDelete; // RECEIVER_DELETE
    
    @Column(name = "SEND_TIME")
    private String sendTime; // SEND_TIME
    
    @Column(name = "RECEIVE_TIME")
    private String receiveTime; // RECEIVE_TIME
    
    @Column(name = "RECEIVER_CHECK")
    private char receiverCheck; // RECEIVER_CHECK
}
