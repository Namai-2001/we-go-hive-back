package com.dev.restLms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user") // 테이블 이름 유지
public class VideoPlayerUser {
    @Id
    // @Column(name = "SESSION_ID") // 물리적 열 이름과 논리적 이름을 동일하게 설정
    private String sessionId;

    @Column(name = "USER_NAME") // 동일한 방식으로 설정
    private String userName;
}