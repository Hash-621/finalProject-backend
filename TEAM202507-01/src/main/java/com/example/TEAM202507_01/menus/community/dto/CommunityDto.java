package com.example.TEAM202507_01.menus.community.dto; // ★ 패키지명 확인!

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

// JPA 어노테이션(@Entity, @Table 등)은 모두 지우고
// 순수한 자바 데이터 객체(DTO)로 사용합니다.
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommunityDto {
    private Long id;            // PK
    private String title;       // 제목
    private String content;     // 내용
    private LocalDateTime createdAt; // 작성일
    private String userId;
    private String category;
    private LocalDateTime updatedAt;
    private Long viewCount;
    private String userNickname;

}