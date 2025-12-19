package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import java.util.List;

public interface CommunityService {
    // ================= 게시글 관련 =================
    // 구현체(ServiceImpl)의 메서드 이름과 똑같이 맞춰야 합니다.
    List<CommunityDto> findAllPosts();
    CommunityDto findPostById(Long id);
    void savePost(CommunityDto dto);
    void deletePost(Long id);
    List<CommunityDto> findPostsByCategory(String category);
    // ================= 댓글 관련 (이게 없어서 오류남) =================
    List<CommentDto> findCommentsByPostId(Long postId);
    void saveComment(CommentDto dto);
    void deleteComment(Long id);
}