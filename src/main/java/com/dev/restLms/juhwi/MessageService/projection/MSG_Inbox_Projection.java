package com.dev.restLms.juhwi.MessageService.projection;

// 받은 쪽지함 관점 조회 프로젝션
public interface MSG_Inbox_Projection {

    // 쪽지 고유 키
    String getMessageId();

    // 제목
    String getMessageTitle();

    // 보낸 사람 고유 키
    String getSenderSessionId();

    // 받은 쪽지함에서 삭제 여부
    String getReceiverDelete();

    // 받은 시간 표시
    String getReceiveTime();

    // 받은 쪽지함 읽음 표시
    String getReceiverCheck();
}
