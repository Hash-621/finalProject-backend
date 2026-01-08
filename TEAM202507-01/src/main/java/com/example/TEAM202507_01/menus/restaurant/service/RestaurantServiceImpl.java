package com.example.TEAM202507_01.menus.restaurant.service;

import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantMapper restaurantMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDto> findAll() {
        List<RestaurantDto> fixedList = restaurantMapper.findAll();

        for (RestaurantDto restaurantDto : fixedList) {
            // 별도로 만든 메서드를 호출해서 데이터 채움
            fillMissingData(restaurantDto);
        }
        return fixedList;
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDto findById(Long id) {
        RestaurantDto restaurantDto = restaurantMapper.findById(id);
        if (restaurantDto == null) {
            throw new RuntimeException("해당 맛집을 찾을 수 없습니다. ID: " + id);
        }

        // 별도로 만든 메서드를 호출해서 데이터 채움
        fillMissingData(restaurantDto);

        return restaurantDto;
    }

    @Override
    public RestaurantDto save(RestaurantDto restaurant) {
        if (restaurant.getId() == null) {
            restaurantMapper.save(restaurant);
        } else {
            restaurantMapper.update(restaurant);
        }
        return restaurant;
    }

    @Override
    public void delete(Long id) {
        restaurantMapper.delete(id);
    }

    // 🔥 [핵심] 중복 로직을 메서드로 분리하고, 리스트를 지역변수로 변경함
    private void fillMissingData(RestaurantDto restaurantDto) {
        // 1. 메뉴 채우기 로직
        if (restaurantDto.getMenu() == null || restaurantDto.getMenu().isEmpty()) {
            // 🔥 여기서 매번 새로 생성해야 식당마다 서로 다른 메뉴판을 가질 수 있음
            List<String> menuList = new ArrayList<>();

            String category = restaurantDto.getRestCategory();
            String name = restaurantDto.getName();

            // NullPointerException 방지를 위해 category가 null인지 체크하는 것이 좋습니다.
            if (category != null) {
                if (category.equals("한식")) {
                    if (name.contains("족발")) {
                        menuList.add("족발");
                        menuList.add("수육");
                    } else if (name.contains("만두")) {
                        menuList.add("갈비만두");
                        menuList.add("찐만두");
                        menuList.add("군만두");
                    } else if (name.contains("찜")) {
                        menuList.add("김치찜");
                        menuList.add("아구찜");
                    } else if (name.contains("찌개")) {
                        menuList.add("김치찌개");
                        menuList.add("된장찌개");
                    } else if (name.contains("고기")) {
                        menuList.add("삼겹살");
                        menuList.add("목살");
                    } else if (name.contains("국수")) {
                        menuList.add("칼국수");
                        menuList.add("비빔국수");
                    } else if (name.contains("김밥")) {
                        menuList.add("김밥");
                        menuList.add("참치김밥");
                        menuList.add("꼬마김밥");
                    }
                    // 기본 한식 메뉴 추가
                    menuList.add("국밥");
                    menuList.add("특대국밥");
                    menuList.add("갈비탕");

                } else if (category.equals("일식")) {
                    menuList.add("돈까스");
                    menuList.add("회");
                    menuList.add("우동");
                } else if (category.equals("중식")) {
                    menuList.add("짜장면");
                    menuList.add("짬뽕");
                    menuList.add("탕수육");
                } else if (category.equals("양식")) {
                    menuList.add("파스타");
                    menuList.add("스테이크");
                } else if (category.equals("분식")) {
                    menuList.add("떡볶이");
                    menuList.add("순대");
                    menuList.add("어묵");
                    menuList.add("김밥");
                } else if (category.equals("치킨")) {
                    menuList.add("치킨");
                    menuList.add("양념치킨");
                } else if (category.equals("카페·디저트")) {
                    menuList.add("아메리카노");
                    menuList.add("카페라떼");
                    menuList.add("바닐라라떼");
                }
            }
            // 완성된 리스트를 DTO에 저장
            restaurantDto.setMenu(menuList);
        }

        // 2. 베스트 메뉴 채우기 로직
        if (restaurantDto.getBestMenu() == null || restaurantDto.getBestMenu().isEmpty()) {
            String category = restaurantDto.getRestCategory();
            String name = restaurantDto.getName();

            if (category != null) {
                if (category.equals("한식")) {
                    if (name.contains("족발")) restaurantDto.setBestMenu("족발");
                    else if (name.contains("만두")) restaurantDto.setBestMenu("갈비만두");
                    else if (name.contains("찜")) restaurantDto.setBestMenu("김치찜");
                    else if (name.contains("찌개")) restaurantDto.setBestMenu("김치찌개");
                    else if (name.contains("고기")) restaurantDto.setBestMenu("삼겹살");
                    else if (name.contains("국수")) restaurantDto.setBestMenu("칼국수");
                    else if (name.contains("김밥")) restaurantDto.setBestMenu("김밥");
                    else restaurantDto.setBestMenu("국밥"); // 기본값
                } else if (category.equals("일식")) {
                    restaurantDto.setBestMenu("돈까스");
                } else if (category.equals("중식")) {
                    restaurantDto.setBestMenu("짜장면");
                } else if (category.equals("양식")) {
                    restaurantDto.setBestMenu("파스타");
                } else if (category.equals("분식")) {
                    restaurantDto.setBestMenu("떡볶이");
                } else if (category.equals("치킨")) {
                    restaurantDto.setBestMenu("치킨");
                } else if (category.equals("카페·디저트")) {
                    restaurantDto.setBestMenu("아메리카노");
                }
            }
        }
    }
}