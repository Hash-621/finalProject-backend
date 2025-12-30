package com.example.TEAM202507_01.menus.community.repository;

import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {

    // 게시글 목록 조회
    List<CommunityDto> selectPostsByCategory(@Param("category") String category);

    // 게시글 상세 조회
    CommunityDto selectPostById(@Param("id") Long id);

    // 게시글 저장
    void insertPost(CommunityDto dto);

    // 댓글 목록 조회
    List<CommentDto> selectCommentsByPostId(@Param("postId") Long postId);

    // 댓글 저장
    void insertComment(CommentDto dto);

    // 댓글 삭제
    void deleteComment(@Param("id") Long id);

    // (선택) 전체 게시글 조회 (Service 인터페이스 에러 해결용)
    List<CommunityDto> findAll();

    void deletePost(@Param("id") Long id);

}