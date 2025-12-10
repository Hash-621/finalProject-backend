package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.menus.community.entity.Community;
import com.example.TEAM202507_01.menus.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository; // Mapper 대신 Repository 주입

    @Override
    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    @Override
    public Community findById(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    @Override
    public Community save(Community community) {
        return communityRepository.save(community);
    }

    @Override
    public void delete(Long id) {
        communityRepository.deleteById(id);
    }
}