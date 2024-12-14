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
public class QuestionBoard {
    private String boardId;

    private String boardCategory;

    private String teacherSessionId;

    private String offeredSubjectsId;
}
