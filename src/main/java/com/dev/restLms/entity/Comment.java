package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
public class Comment {

    @Id
    private String commentId;

    private String authorNickname;
    private String createdDate;
    private String content;
    private String rootCommentId;
    private String postId;
    private String sessionId;
    private String previousCommentId;
    private String isSecret;

    @PrePersist
    public void generateUUID() {
        if (commentId == null) {
            commentId = UUID.randomUUID().toString();
        }
    }
}
