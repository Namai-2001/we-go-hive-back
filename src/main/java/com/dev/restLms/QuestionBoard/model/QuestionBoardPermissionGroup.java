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
@Table(name = "permissiongroup")
public class QuestionBoardPermissionGroup {

    @Id
    @Column(name = "PERMISSION_GROUP_UUID")
    private String permissionGroupUuid;

    @Column(name = "PERMISSION_NAME")
    private String permissionName;
    
}
