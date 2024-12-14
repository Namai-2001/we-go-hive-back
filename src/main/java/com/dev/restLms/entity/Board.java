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
public class Board {

    @Id
    private String boardId;

    private String boardCategory;
    private String teacherSessionId;
    private String offeredSubjectsId;

    @PrePersist
    public void generateUUID() {
        if (boardId == null) {
            boardId = UUID.randomUUID().toString();
        }
    }
}
