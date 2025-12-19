package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.common.service.CleanBotService; // 🟢 import 추가
import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.repository.CommentMapper;
import com.example.TEAM202507_01.menus.community.repository.CommunityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper communityMapper;
    private final CommentMapper commentMapper;
    private final CleanBotService cleanBotService; // 🟢 클린봇 서비스 주입

    // ==========================================
    // 📢 게시글 (Post) 관련 기능
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<CommunityDto> findAllPosts() {
        return communityMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityDto> findPostsByCategory(String category) {
        return communityMapper.findByCategory(category);
    }

    @Override
    public CommunityDto findPostById(Long id) {
        communityMapper.increaseViewCount(id);

        CommunityDto post = communityMapper.findById(id);
        if (post == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        return post;
    }

    @Override
    public void savePost(CommunityDto dto) {
        // 🟢 1. 욕설 필터링 적용 (제목, 내용)
        // 욕설이 포함되어 있으면 여기서 예외가 발생하여 저장이 중단됩니다.
        cleanBotService.checkContent(dto.getTitle());
        cleanBotService.checkContent(dto.getContent());

        // 2. 카테고리 누락 방지
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            dto.setCategory("FREE");
        }

        // 3. 저장 또는 수정
        if (dto.getId() == null) {
            log.info("새 게시글 등록: {}", dto.getTitle());
            communityMapper.save(dto);
        } else {
            log.info("게시글 수정: {}", dto.getId());
            communityMapper.update(dto);
        }
    }

    @Override
    public void deletePost(Long id) {
        log.info("게시글 삭제: {}", id);
        communityMapper.delete(id);
    }

    // ==========================================
    // 💬 댓글 (Comment) 관련 기능
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findCommentsByPostId(Long postId) {
        return commentMapper.findAllByPostId(postId);
    }

    @Override
    public void saveComment(CommentDto dto) {
        // 🟢 1. 욕설 필터링 적용 (댓글 내용)
        cleanBotService.checkContent(dto.getContent());

        // 2. 저장 또는 수정
        if (dto.getId() == null) {
            log.info("새 댓글 등록 - 게시글ID: {}", dto.getPostId());
            commentMapper.save(dto);
        } else {
            log.info("댓글 수정 - 댓글ID: {}", dto.getId());
            commentMapper.update(dto);
        }
    }

    @Override
    public void deleteComment(Long id) {
        log.info("댓글 삭제: {}", id);
        commentMapper.delete(id);
    }
}