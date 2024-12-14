package com.dev.restLms.QuestionBoardPost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBoardPostBoardPost {

    private String postId;

    private String authorNickname;

    private String createdDate;

    private String title;

    private String content;

    private String boardId;

    private String sessionId;

    private String isNotice;

}
