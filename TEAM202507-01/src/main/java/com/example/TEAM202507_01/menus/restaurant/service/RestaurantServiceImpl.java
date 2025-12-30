package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper; // Mapper Import
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service // 비즈니스 로직을 담당하는 서비스임을 명시.
@RequiredArgsConstructor // final 필드(Mapper)를 생성자 주입으로 받게 해줌.
@Transactional // 메서드 실행 중 에러 발생 시 자동 롤백(취소) 처리.
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantMapper restaurantMapper;

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션. 조회 성능 최적화.
    public List<RestaurantDto> findAll() {
        return restaurantMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDto findById(Long id) {
        RestaurantDto restaurant = restaurantMapper.findById(id);
        if (restaurant == null) {
            // 찾는 식당이 없으면 예외를 발생시켜서 멈춤.
            throw new RuntimeException("해당 맛집을 찾을 수 없습니다. ID: " + id);
        }
        return restaurant;
    }

    @Override
    public RestaurantDto save(RestaurantDto restaurant) {
        // ID가 없으면 '새로 등록'으로 간주 -> save()
        if (restaurant.getId() == null) {
            restaurantMapper.save(restaurant);
        } else {
            // ID가 있으면 '수정'으로 간주 -> update()
            restaurantMapper.update(restaurant);
        }
        return restaurant;
    }

    @Override
    public void delete(Long id) {
        restaurantMapper.delete(id);
    }
}
