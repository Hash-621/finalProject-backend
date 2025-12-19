package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import java.util.List;

public interface RestaurantService {
    List<RestaurantDto> findAll();
    RestaurantDto findById(Long id);
    RestaurantDto save(RestaurantDto restaurant);
    void delete(Long id);
    // 🔥 [추가] 외부 API 데이터 동기화 메서드
    String syncRestaurantData();
}