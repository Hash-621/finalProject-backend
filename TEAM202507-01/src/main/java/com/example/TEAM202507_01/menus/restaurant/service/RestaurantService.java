package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import java.util.List;

// 설계도 역할. 구현체(ServiceImpl)가 어떤 기능을 가져야 하는지 정의함.
public interface RestaurantService {
    List<RestaurantDto> findAll();
    RestaurantDto findById(Long id);
    RestaurantDto save(RestaurantDto restaurant);
    void delete(Long id);
}