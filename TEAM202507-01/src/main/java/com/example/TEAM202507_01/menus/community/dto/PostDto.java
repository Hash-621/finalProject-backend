package com.example.TEAM202507_01.menus.community.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// 게시글 정보를 담는 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id; //게시글의 주민번호(고유번호)
    private String userId; // 작성자의 진짜 아이디
    private String userNickname; //화면에 보여줄 작성자 닉네임
    private String category; //이 글이 자유,질문,정보,유머 등 어디에 속하는 지 분류표
    private String title; // 글제목
    private String content; //본문 내용
    private Long viewCount; //조회수
    private Integer likeCount; //좋아요 개수
    private LocalDateTime createdAt; //작성일
    private LocalDateTime updatedAt; //수정일

    // 게시글 상세 조회 시 댓글 목록을 포함할 경우 사용
    private List<CommentDto> comments; // 댓글 목록
}

