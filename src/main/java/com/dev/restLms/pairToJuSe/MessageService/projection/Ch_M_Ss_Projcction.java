package com.dev.restLms.pairToJuSe.MessageService.projection;

// 보낸 쪽지함 프로젝션
public interface Ch_M_Ss_Projcction {

    // 쪽지 고유 키
    String getMessageId();

    // 제목
    String getMessageTitle();

    // 받은 대상
    String getReceiverSessionId();

    // 보낸 쪽지함에서 삭제 여부
    String getSenderDelete();

    // 보낸 시간 표시
    String getSendTime();
}
