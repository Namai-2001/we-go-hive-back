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
public class QuestionBoardPostComment {

    private String commentId;

    private String authorNickname;

    private String createdDate;

    private String content;

    private String postId;

    private String sessionId;
    
}
