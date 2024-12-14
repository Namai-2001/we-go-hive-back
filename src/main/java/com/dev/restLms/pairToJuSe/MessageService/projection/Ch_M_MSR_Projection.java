package com.dev.restLms.pairToJuSe.MessageService.projection;

public interface Ch_M_MSR_Projection {
    // 쪽지 고유 키
    String getMessageId();

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
}
