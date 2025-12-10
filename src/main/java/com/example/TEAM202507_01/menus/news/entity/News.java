package com.example.TEAM202507_01.menus.news.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NEWS") // DB 테이블 이름
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String publisher; // 언론사

    @Column(name = "published_at")
    private LocalDateTime publishedAt; // 보도 일시
}