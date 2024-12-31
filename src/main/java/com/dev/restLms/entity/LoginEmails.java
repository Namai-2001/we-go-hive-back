package com.dev.restLms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginEmails  {
    @Id
    String emailUuid;
    String sessionId;
    String email;
    String createdAt;
}
