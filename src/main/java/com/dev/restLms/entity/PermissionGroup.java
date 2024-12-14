package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionGroup {
    @Id
    String permissionGroupUuid;
    String permissionName;
    String componentName;
    String endpointUrl;

    @PrePersist
    public void generateUUID() {
        if (permissionGroupUuid == null) {
            permissionGroupUuid = UUID.randomUUID().toString();
        }
    }
}