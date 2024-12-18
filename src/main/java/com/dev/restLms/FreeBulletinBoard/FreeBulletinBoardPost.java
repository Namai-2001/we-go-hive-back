package com.dev.restLms.FreeBulletinBoard;

public interface FreeBulletinBoardPost {

    String getPostId();

    String getAuthorNickname();

    String getCreatedDate();

    String getTitle();

    String getContent();

    String getBoardId();

    String getSessionId();

    String getIsNotice();
    
}
