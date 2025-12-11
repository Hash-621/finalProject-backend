package com.example.TEAM202507_01.menus.tour.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TourPostCommentDto {
    private Long reviewId; // 리뷰 ID review_id
    private Long postId; // 게시글 ID rest_id
    private String userId; // 작성자 ID user_id
    private String content; // 리뷰 내용 content_id
    private boolean isDelete; // 삭제여부 is_delete
    private LocalDate createdAt; // 작성일시 created_at
}