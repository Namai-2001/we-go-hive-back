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
@Table(name = "userownsubjectvideo")
public class ProcessListUserOwnSubjectVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "INCREASE_ID")
    private int increaseId;

    @Column(name = "UOSV_SESSION_ID")
    private String uosvSessionId;

    @Column(name = "UOSV_EPISODE_ID")
    private int uosvEpisodeId;

    @Column(name = "UOSV_OFFERED_SUBJECTS_ID")
    private String uosvOfferedSubjectsId;

    @Column(name = "PROGRESS")
    private int progress;
}
