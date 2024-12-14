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
public class QuestionBoardPermissionGroup {

    private String permissionGroupUuid;

    private String permissionName;
    
}
