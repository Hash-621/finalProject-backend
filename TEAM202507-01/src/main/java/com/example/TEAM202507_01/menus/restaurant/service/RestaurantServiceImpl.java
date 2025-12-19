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

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantMapper restaurantMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    // JSON 변환기 (설정 추가: 모르는 필드 있어도 에러 내지 마라)
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


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
    @Transactional
    public String syncRestaurantData() {
        System.out.println("========== [동기화 시작] ==========");
        int totalSuccess = 0;

        // 🔥 [핵심 1] 브라우저인 척 속이기 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        for (int i = 1; i <= 10; i++) {
            String url = "https://bigdata.daejeon.go.kr/api/stores/?page=" + i;
            System.out.println("\n>> " + i + "페이지 요청 중: " + url);

            try {
                // 🔥 [핵심 2] exchange 메서드로 헤더를 포함해서 요청
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        URI.create(url), HttpMethod.GET, entity, String.class);

                String jsonString = responseEntity.getBody();

                // 🔍 [디버깅] 진짜 데이터가 왔는지 눈으로 확인
                if (jsonString == null || jsonString.isEmpty()) {
                    System.out.println("🚨 " + i + "페이지 응답이 비어있음 (NULL/Empty)");
                    continue;
                }
                // 앞부분 300자만 찍어서 확인
                System.out.println("🔍 응답 내용(앞부분): " + jsonString.substring(0, Math.min(jsonString.length(), 300)));

                // 2. 파싱
                ResponseWrapper response = objectMapper.readValue(jsonString, ResponseWrapper.class);

                if (response == null) {
                    System.out.println("🚨 파싱 실패: response 객체가 NULL");
                    continue;
                }
                if (response.getResults() == null) {
                    System.out.println("🚨 파싱 실패: results 리스트가 NULL (JSON 키 불일치 의심)");
                    continue;
                }
                if (response.getResults().isEmpty()) {
                    System.out.println("⚠️ 파싱 성공했으나 데이터가 0건입니다.");
                    continue;
                }

                System.out.println("✅ 파싱 성공! 데이터 개수: " + response.getResults().size());

                // 3. 저장
                for (RestaurantDto dto : response.getResults()) {
//                    dto.setCategory("RESTAURANT");

                    try {
                        // Null 방지
                        if (dto.getMenu() == null) dto.setMenu(new ArrayList<>());
                        if (dto.getPrice() == null) dto.setPrice(new ArrayList<>());
                        if (dto.getMenuDetail() == null) dto.setMenuDetail(new ArrayList<>());

                        // DB 저장
                        restaurantMapper.save(dto);
                        totalSuccess++;

                        // 첫 번째 데이터만 저장 성공 로그 찍기 (너무 많으니까)
                        if (totalSuccess % 10 == 0) System.out.print(".");

                    } catch (Exception e) {
                        // 에러 로그를 빨간색으로 정확히 출력
                        System.err.println("\n❌ 저장 에러 (ID: " + dto.getName() + "): " + e.getMessage());
                        // e.printStackTrace(); // 필요하면 주석 해제
                    }
                }

            } catch (Exception e) {
                System.err.println("\n💥 API 호출 중 에러: " + e.getMessage());
                e.printStackTrace();
            }
        }

        String resultMsg = "\n========== [동기화 종료] 총 " + totalSuccess + "건 저장됨 ==========";
        System.out.println(resultMsg);

        return resultMsg;
    }

    // 내부 클래스 (static 필수)
    @Data
    public static class ResponseWrapper {
        private int count;
        private String next;

        @JsonAlias("results") // JSON의 "results" 키와 매핑
        private List<RestaurantDto> results;
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