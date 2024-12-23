package com.dev.restLms.juhwi.MessageService.projection;

public interface MSG_Self_Projection {
    // 쪽지 고유 키
    String getMessageId();

    // 닉네임 변환을 위한 ID 가져오기
    String getSenderSessionId();

    // 닉네임 변환을 위한 ID 가져오기
    String getReceiverSessionId();

    // 내게 쓴 제목
    String getMessageTitle();

    // 내게 쓴 쪽지함 삭제 여부
    String getSenderDelete();

    // 휴지통 여부
    String getReceiverDelete();

    // 내게 쓴 쪽지 시간 표시
    String getSendTime();

    // 내게 쓴 쪽지 시간 표시
    String getReceiveTime();

    // 내게 쓴 쪽지함 읽음 표시
    String getReceiverCheck();

    // 내게 쓴 쪽지함 보낸 필드까지 읽음 표시
    String getSenderCheck();
}
