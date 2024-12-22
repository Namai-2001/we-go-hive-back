package com.dev.restLms.juhwi.MessageService.projection;

public interface MSG_Details_Projection {
    // 식별 키
    String getMessageId();

    // 제목
    String getMessageTitle();

    // 메시지 내용
    String getMessageContent();

    // 보낸 사람 ID
    String getSenderSessionId();

    // 받은 사람 ID
    String getReceiverSessionId();

    // 받은 시간
    String getSendTime();

    // 보낸 시간
    String getReceiveTime();
}
