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
public class BoardPost {

    @Id
    private String postId;

    private String authorNickname;
    private String createdDate;
    private String title;
    private String content;
    private String boardId;
    private String sessionId;
    private String isNotice;
    private String isSecret;
    private String fileNo;

    @PrePersist
    public void generateUUID() {
        if (postId == null) {
            postId = UUID.randomUUID().toString();
        }
    }
}
