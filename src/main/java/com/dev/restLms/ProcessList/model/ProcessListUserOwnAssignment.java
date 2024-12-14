package com.dev.restLms.ProcessList.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "userownassignment")
public class ProcessListUserOwnAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "INCREASE_ID")
    private int increaseId;
    
    @Column(name = "USER_SESSION_ID")
    private String userSessionId;

    @Column(name = "OFFERED_SUBJECTS_ID")
    private String offeredSubjectsId;

    @Column(name = "SUBJECT_ACCEPT_CARTEGORY")
    private String subjectAcceptCartegory;
    
}
