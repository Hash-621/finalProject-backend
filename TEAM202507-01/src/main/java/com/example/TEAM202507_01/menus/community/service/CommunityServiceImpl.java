package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.common.service.CleanBotService;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.repository.CommunityMapper;
import com.example.TEAM202507_01.search.document.SearchDocument;
import com.example.TEAM202507_01.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper communityMapper;
    private final CleanBotService cleanBotService;
    private final SearchService searchService; // ★ 검색 서비스 주입

    @Override
    @Transactional(readOnly = true)
    public List<CommunityDto> findAll() {
        return communityMapper.findAll();
    }

    @Override
    public CommunityDto findById(Long id) {
        communityMapper.increaseViewCount(id);
        CommunityDto community = communityMapper.findById(id);
        if (community == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
        return community;
    }

    @Override
    public CommunityDto save(CommunityDto community) {
        // 1. 클린봇 검사 (욕설 필터링)
        cleanBotService.checkContent(community.getTitle());
        cleanBotService.checkContent(community.getContent());

        // 2. DB 저장 (INSERT or UPDATE)
        if (community.getId() == null) {
            communityMapper.save(community); // ID가 여기서 생성됨
        } else {
            communityMapper.update(community);
        }

        // 3. ★ 엘라스틱서치 동기화 (검색 엔진에 저장)
        try {
            SearchDocument doc = SearchDocument.builder()
                    .id("POST_" + community.getId()) // ID 충돌 방지용 접두사
                    .originalId(community.getId())
                    .category("COMMUNITY")
                    .title(community.getTitle())
                    .content(community.getContent()) // 본문 내용 전체 저장 (검색용)
                    .url("/community/free/" + community.getId()) // 클릭 시 이동할 URL
                    .build();
            searchService.saveDocument(doc);
        } catch (Exception e) {
            System.err.println("검색 엔진 저장 실패 (DB는 성공함): " + e.getMessage());
            // 검색 저장이 실패해도 메인 로직은 성공 처리 (선택 사항)
        }

        return community;
    }

    @Override
    public void delete(Long id) {
        communityMapper.delete(id);
        // (선택사항) 삭제 시 엘라스틱서치에서도 지우는 로직이 필요하다면 여기에 추가
        // searchService.deleteDocument("POST_" + id);
    }
}