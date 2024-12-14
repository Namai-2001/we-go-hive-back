package com.dev.restLms.QuestionBoard.model;

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
@Table(name = "subject")
public class QuestionBoardSubject {

    @Id
    @Column(name = "SUBJECT_ID")
    private String subjectId;

    @Column(name = "SUBJECT_NAME")
    private String subjectName;
    
}
