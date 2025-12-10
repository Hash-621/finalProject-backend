package com.example.TEAM202507_01.menus.community.repository;

import com.example.TEAM202507_01.menus.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<엔티티타입, PK타입> 상속받으면 끝! (SQL 안 짜도 됨)
@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 기본 CRUD(save, findAll, findById, deleteById)는 이미 다 들어있음.
    // 추가로 필요한 쿼리 메서드만 여기에 작성

    // 예: 제목으로 검색
    // List<Community> findByTitleContaining(String keyword);
}