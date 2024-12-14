package com.dev.restLms.QuestionBoard;

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
public class QuestionBoardOfferedSubjects {

    private String offeredSubjectsId;

    private String teacherSessionId;

    private String subjectId;

    private String officerSessionId;
    
}
