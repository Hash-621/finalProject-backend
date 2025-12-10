package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.menus.community.entity.Community;
import java.util.List;

public interface CommunityService {
    List<Community> findAll();
    Community findById(Long id);
    Community save(Community community);
    void delete(Long id);
}