package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.repository.CommunityMapper;
import com.example.TEAM202507_01.user.repository.MyPageMapper;
// ★ 아래 import 경로가 위에서 만든 파일 경로와 일치해야 에러가 사라집니다.
import com.example.TEAM202507_01.cleanbot.service.CleanBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper communityMapper;
    private final MyPageMapper myPageMapper;
    private final CleanBotService cleanBotService; // 이제 인식될 것입니다.

    @Override
    public List<CommunityDto> findAllPosts() {
        return new ArrayList<>();
    }

    @Override
    public List<CommunityDto> findPostsByCategory(String category) {
        return communityMapper.selectPostsByCategory(category);
    }

    @Override
    public CommunityDto findPostById(Long id) {
        return communityMapper.selectPostById(id);
    }

    @Override
    @Transactional
    public void savePost(CommunityDto dto) {
        if (cleanBotService != null) {
            cleanBotService.checkContent(dto.getTitle());
            cleanBotService.checkContent(dto.getContent());
        }
        communityMapper.insertPost(dto);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        communityMapper.deletePost(id);
    }

    @Override
    public List<CommentDto> findCommentsByPostId(Long postId) {
        return communityMapper.selectCommentsByPostId(postId);
    }

    @Override
    @Transactional
    public void saveComment(CommentDto dto) {
        // 1. 욕설 필터링
        if (cleanBotService != null) {
            cleanBotService.checkContent(dto.getContent());
        }

        // 2. 로그인 ID -> UUID 변환
        String loginId = dto.getUserId();
        log.info("🔍 댓글 작성 시도 - 로그인ID: {}", loginId);

        String uuid = myPageMapper.findUuidByLoginId(loginId);

        if (uuid == null) {
            log.error("❌ 유저 정보를 찾을 수 없습니다. loginId: {}", loginId);
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }

        log.info("✅ UUID 변환 성공: {} -> {}", loginId, uuid);

        // 3. 변환된 UUID로 교체 후 저장
        dto.setUserId(uuid);
        communityMapper.insertComment(dto);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        communityMapper.deleteComment(id);
    }
}