package com.example.TEAM202507_01.menus.community.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity // 1. JPA가 관리하는 엔티티임
@Table(name = "COMMUNITY") // 2. DB 테이블 이름
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community {

    @Id // PK (기본키)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT") // 긴 글
    private String content;

    private String writer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 생성될 때 시간 자동 저장
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}