package com.example.TEAM202507_01.menus.tour.repository;

import com.example.TEAM202507_01.menus.tour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    // 기본 CRUD(저장, 조회, 수정, 삭제)는 자동으로 제공됩니다.
    // 추가로 필요한 검색 기능 등이 있다면 여기에 메서드 이름만 적으면 됩니다.
    // 예: List<Tour> findByNameContaining(String keyword);
}