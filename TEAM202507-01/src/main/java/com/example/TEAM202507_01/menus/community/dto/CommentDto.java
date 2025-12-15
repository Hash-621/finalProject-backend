package com.example.TEAM202507_01.menus.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data //getter setter tostring()같은 필수 기능들을 자동으로 만들어줌
@Builder //객체를 만들때 이름표를 붙여서 쉽게 만들수 있게 해주는 기능
@NoArgsConstructor //빈깡통 기본생성자를 만들어줌
@AllArgsConstructor // 모든 필드를 다 채울수있는 꽉 찬 생성자를 만듬
public class CommentDto {
    private Long id; //댓글 주민번호
    private Long postId; //어느 게시글에 달린 댓글인지 나타내는 번호
    private String userId; //고유 id
    private String userNickname; //화면에 보여줄 작성자 닉네임
    private String content; // 댓글의 내용
    private Integer isDelete; //삭제 여부 스위치
    private LocalDateTime createdAt; // 작성 시간
}
