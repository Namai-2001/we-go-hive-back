package com.dev.restLms.juhwi.messagepage.projection;

/* 의도 주입
 * 1. 세션 아이디를 닉네임으로 변환하기 위한 의도 주입
 * 2. 대조하기 위한 두 값 조회 의도 주입
 */
public interface MessagepageParseNicknameProjection {
    String getSessionId();
    String getNickname();
}