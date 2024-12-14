package com.dev.restLms.QuestionBoardPost.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment")
public class QuestionBoardPostComment {

    @Id
    @Column(name = "COMMENT_ID")
    private String commentId;

    @Column(name = "AUTHOR_NICKNAME")
    private String authorNickname;

    @Column(name = "CREATED_DATE")
    private String createdDate;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "POST_ID")
    private String postId;

    private String sessionId;
    
}
