package com.example.TEAM202507_01.menus.tour.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TourPostDto {
    private Long id; // 게시글 ID id
    private String userId; // 작성자 ID user_id
    private String category; // 카테고리 category
    private String title; // 제목 title
    private String content; // 내용 content
    private Long viewCount  ; // 조회수 view_count
    private LocalDateTime createdAt; // 작성일시 CREATED_AT
    private LocalDateTime updatedAt; // 수정일시 UPDATED_AT

    private List<TourPostCommentDto> comments; // 댓글 목록
}







