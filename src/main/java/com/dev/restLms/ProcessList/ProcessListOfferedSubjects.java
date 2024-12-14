package com.dev.restLms.ProcessList;

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
public class ProcessListOfferedSubjects {
	private String offeredSubjectsId;

	private String courseId; 
	
	private String officerSessionId ;
	
	private String subjectId;
}
