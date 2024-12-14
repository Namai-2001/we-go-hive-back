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
@Table(name = "userownpermissiongroup")
public class QuestionBoardUserOwnPermissionGroup {

    @Id
    @Column(name = "PERMISSION_GROUP_UUID2")
    private String permissionGroupUuid2;

    private String sessionId;
    
}
