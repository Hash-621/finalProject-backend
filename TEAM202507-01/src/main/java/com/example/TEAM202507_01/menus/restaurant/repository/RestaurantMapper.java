package com.example.TEAM202507_01.menus.restaurant.repository;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

// @Mapper: 스프링이 실행될 때 MyBatis 구현체를 자동으로 생성해서 Bean으로 등록해줌.
@Mapper
public interface RestaurantMapper {

    // 1. 전체 조회: DB에 있는 모든 식당을 가져옴.
    List<RestaurantDto> findAll();

    // 2. 상세 조회: ID로 특정 식당 하나만 가져옴.
    RestaurantDto findById(Long id);

    // 이름 검색용 (크롤링 시 식당 이름으로 정보를 찾기 위해 사용)
    RestaurantDto findNameById(Long id);

    // 3. 등록: 식당 정보를 DB에 저장함 (Insert).
    void save(RestaurantDto restaurant);

    // 4. 수정: 식당 정보를 업데이트함 (Update).
    void update(RestaurantDto restaurant);

    // 5. 삭제: 식당을 삭제함 (Delete).
    void delete(Long id);

    // 6. 이미지 업데이트
    // @Param: 파라미터가 2개 이상일 때, XML(SQL)에서 #{imageId}, #{imagePath}로 구분해서 쓰기 위해 이름을 붙여줌.
    void updateImage(@Param("imageId") Long id, @Param("imagePath") String imagePath);

    // 7. URL이 있는 식당만 조회 (이미지 크롤링 대상 추출용)
    List<RestaurantDto> findAllWithUrl();
}
