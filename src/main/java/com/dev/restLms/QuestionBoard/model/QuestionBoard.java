package com.dev.restLms.QuestionBoard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
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
@Table(name = "board")
public class QuestionBoard {
    @Id
    @Column(name="BOARD_ID")
    private String boardId;

    @Column(name = "BOARD_CATEGORY")
    private String boardCategory;

    @Column(name = "TEACHER_SESSION_ID")
    private String teacherSessionId;

    @Column(name = "OFFERED_SUBJECTS_ID")
    private String offeredSubjectsId;
}
