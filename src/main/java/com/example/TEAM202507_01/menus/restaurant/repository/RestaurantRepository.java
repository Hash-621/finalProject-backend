package com.example.TEAM202507_01.menus.restaurant.repository;

import com.example.TEAM202507_01.menus.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // 기본 CRUD는 자동 제공됩니다.
    // 추가 쿼리가 필요하면 여기에 작성 (예: 이름으로 검색)
    // List<Restaurant> findByNameContaining(String keyword);
}