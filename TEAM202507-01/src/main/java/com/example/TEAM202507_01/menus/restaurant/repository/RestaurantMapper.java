package com.example.TEAM202507_01.menus.restaurant.repository;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RestaurantMapper {

    // 1. 전체 조회
    List<RestaurantDto> findAll();

    // 2. 상세 조회
    RestaurantDto findById(Long id);

    // 3. 등록 (Insert)
    void save(RestaurantDto restaurant);

    // 4. 수정 (Update)
    void update(RestaurantDto restaurant);

    // 5. 삭제 (Delete)
    void delete(Long id);
}