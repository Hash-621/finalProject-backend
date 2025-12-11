package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper; // Mapper Import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantMapper restaurantMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDto> findAll() {
        return restaurantMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDto findById(Long id) {
        RestaurantDto restaurant = restaurantMapper.findById(id);
        if (restaurant == null) {
            throw new RuntimeException("해당 맛집을 찾을 수 없습니다. ID: " + id);
        }
        return restaurant;
    }

    @Override
    public RestaurantDto save(RestaurantDto restaurant) {
        if (restaurant.getId() == null) {
            restaurantMapper.save(restaurant); // 신규 등록
        } else {
            restaurantMapper.update(restaurant); // 수정
        }
        return restaurant;
    }

    @Override
    public void delete(Long id) {
        restaurantMapper.delete(id);
    }
}