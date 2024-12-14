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
@Table(name = "subjectownvideo")
public class ProcessListSubjectOwnVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "EPISODE_ID")
	private int episodeId;

	@Column(name = "SOV_OFFERED_SUBJECTS_ID")
	private String sovOfferedSubjectsId;

}
