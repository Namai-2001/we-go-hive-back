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
public class ProcessListUserOwnAssignment {

    private String increaseId;
    
    private String userSessionId;

    private String offeredSubjectsId;

    private String subjectAcceptCartegory;
    
}
