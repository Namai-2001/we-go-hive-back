package com.dev.restLms.juhwi.MessageService.projection;

public interface Ch_M_MASR_Projection {

    // 쪽지 고유 키
    String getMessageId();

    // 보낸 사람
    String getSenderSessionId();

    // 제목
    String getMessageTitle();

    // 공지 삭제 여부
    String getSenderDelete();

    // 관리자 공지 삭제 여부
    String getReceiverDelete();

    // 관리자 공지 작성 시간
    String getSendTime();

    // 관리자 공지 작성 시간
    String getReceiveTime();

    // 공지 읽음 여부
    String getReceiverCheck();
}
